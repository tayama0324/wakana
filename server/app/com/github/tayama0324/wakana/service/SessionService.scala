package com.github.tayama0324.wakana.service

import java.security.SecureRandom

import com.github.tayama0324.wakana.entity.Session
import com.github.tayama0324.wakana.session.{GetSessionByIdRequest, GetSessionByIdResponse, LoginWithTwitterOAuthVerifierRequest, LoginWithTwitterOAuthVerifierResponse}
import com.github.tayama0324.wakana.util.{MixInDefaultExecutionContext, UsesExecutionContext}
import com.google.protobuf.timestamp.Timestamp
import play.utils.Resources
import twitter4j.auth.{AccessToken, RequestToken}
import twitter4j.{Twitter, TwitterFactory}

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future
import scala.io.Source

trait MixInSessionService {
  val sessionService = new SessionService
    with MixInRequestTokenRepository
    with MixInSessionRepository
    with MixInLoginAcl
    with MixInDefaultExecutionContext
}

trait UsesSessionService {
  def sessionService: SessionService
}

abstract class SessionService
  extends UsesRequestTokenRepository
  with UsesSessionRepository
  with UsesLoginAcl
  with UsesExecutionContext {

  private def readResource(name: String): String = {
    println(name + ": " + getClass.getClassLoader.getResource(name))
    Source.fromURL(getClass.getClassLoader.getResource(name)).mkString
  }

  // TODO: Read from file
  lazy val consumerKey = readResource("secret/twitter-consumer-key.txt").trim
  lazy val consumerSecret = readResource("secret/twitter-consumer-secret.txt").trim

  println("Consumer Key: " + consumerKey)
  println("Consumer Secret: " + consumerSecret)

  val random = new SecureRandom()
  val defaultExpire = Timestamp(Long.MaxValue, Int.MaxValue)

  private def newTwitter(): Twitter = {
    val twitter = new TwitterFactory().getInstance()
    twitter.setOAuthConsumer(consumerKey, consumerSecret)
    twitter
  }

  /**
    * TODO: error handling
    */
  def createRequestToken(): RequestToken = {
    val twitter = newTwitter()
    val requestToken = twitter.getOAuthRequestToken("http://localhost:9000/login-callback")
    requestTokenRepository.set(requestToken)
    requestToken
  }

  /**
    * TODO: error handling
    */
  private def getAccessToken(oAuthToken: String, oAuthVerifier: String): Option[AccessToken] = {
    val twitter = newTwitter()
    requestTokenRepository.get(oAuthToken).map { requestToken =>
      twitter.getOAuthAccessToken(requestToken, oAuthVerifier)
    }
  }

  private def createSession(userId: Long): Future[Session] = {
    val session = Session(
      id = random.longs(1, 1, Long.MaxValue).findFirst.orElse(Long.MaxValue),
      userId = userId,
      expireAt = Some(defaultExpire)
    )
    sessionRepository.set(session)
    Future.successful(session)
  }

  private def createSessionIfAuthorized(accessToken: AccessToken): Future[Option[Session]] = {
    if (loginAcl.forTwitterUser(accessToken)) {
      createSession(accessToken.getUserId).map(Some(_))
    } else {
      Future.successful(None)
    }
  }

  def loginWithTwitterOAuthVerifier(request: LoginWithTwitterOAuthVerifierRequest): Future[LoginWithTwitterOAuthVerifierResponse] = {
    getAccessToken(request.oauthToken, request.oauthVerifier) match {
      case Some(accessToken) =>
        createSessionIfAuthorized(accessToken).map { sessionOpt =>
          val status = if (sessionOpt.isDefined) {
            LoginWithTwitterOAuthVerifierResponse.Status.OK
          } else {
            LoginWithTwitterOAuthVerifierResponse.Status.FORBIDDEN
          }
          LoginWithTwitterOAuthVerifierResponse(status, sessionOpt)
        }

      case None =>
        Future.successful(LoginWithTwitterOAuthVerifierResponse(
          status = LoginWithTwitterOAuthVerifierResponse.Status.INVALID_TOKEN
        ))
    }
  }

  def getSessionById(request: GetSessionByIdRequest): Future[GetSessionByIdResponse] = {
    // TODO: Implement appropriately
    def isExpired(session: Session): Boolean = false

    val sessionOpt = sessionRepository.get(request.sessionId) match {
      case Some(session) if isExpired(session) =>
        sessionRepository.delete(request.sessionId)
        None
      case Some(session) => Some(session)
      case None => None
    }
    Future.successful(GetSessionByIdResponse(sessionOpt))
  }
}

abstract class LoginAcl {

  /**
    * Returns true if the user is alloed to login.
    *
    * TODO: implement ACL-based authorization.
    */
  def forTwitterUser(accessToken: AccessToken): Boolean = {
    true
  }
}

trait UsesLoginAcl {
  def loginAcl: LoginAcl
}

trait MixInLoginAcl {
  def loginAcl: LoginAcl = new LoginAcl() {}
}

abstract class RequestTokenRepository {

  // token -> RequestToken
  val tokens: TrieMap[String, RequestToken] = new TrieMap()

  def set(requestToken: RequestToken): Unit = {
    tokens.put(requestToken.getToken, requestToken)
  }

  def get(token: String): Option[RequestToken] = {
    tokens.get(token)
  }
}

object RequestTokenRepository extends RequestTokenRepository

trait UsesRequestTokenRepository {
  def requestTokenRepository: RequestTokenRepository
}

trait MixInRequestTokenRepository {
  val requestTokenRepository = RequestTokenRepository
}

trait UsesSessionRepository {
  def sessionRepository: SessionRepository
}

trait MixInSessionRepository {
  val sessionRepository = SessionRepository
}

abstract class SessionRepository {

  // session.id -> session
  val values = TrieMap.empty[Long, Session]

  def get(id: Long): Option[Session] = values.get(id)

  def set(session: Session): Unit = values.put(session.id, session)

  // Returns true if actually deleted.
  def delete(id: Long): Boolean = values.remove(id).nonEmpty
}

object SessionRepository extends SessionRepository
