package com.github.tayama0324.wakana.controller

import com.github.tayama0324.wakana.entity.Session
import com.github.tayama0324.wakana.service.MixInSessionService
import com.github.tayama0324.wakana.service.SessionRepository
import com.github.tayama0324.wakana.service.UsesSessionService
import com.github.tayama0324.wakana.session.GetSessionByIdRequest
import com.github.tayama0324.wakana.session.LoginWithTwitterOAuthVerifierRequest
import com.github.tayama0324.wakana.util.MixInDefaultExecutionContext
import com.github.tayama0324.wakana.util.UsesExecutionContext
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Cookie
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.mvc.Results

import scala.concurrent.Future
import scala.util.Try

abstract class LoginController extends Controller
  with UsesSessionService
  with UsesCookieBakery
  with UsesExecutionContext {

  def entry(): Action[AnyContent] = Action { request =>
    val requestToken = sessionService.createRequestToken()
    Results.SeeOther(requestToken.getAuthenticationURL)
  }

  def callback(): Action[AnyContent] = Action.async { request =>
    val oauthToken = request.getQueryString("oauth_token").getOrElse("")
    val oauthVerifier = request.getQueryString("oauth_verifier").getOrElse("")

    val loginRequest = LoginWithTwitterOAuthVerifierRequest(oauthToken, oauthVerifier)
    sessionService.loginWithTwitterOAuthVerifier(loginRequest).map { response =>
      val result = Results.Redirect("/login-check")
      response.session match {
        case Some(s) => cookieBakery.attachSession(result, s)
        case None => result
      }
    }
  }

  def loginCheck(): Action[AnyContent] = Action.async { request =>
    cookieBakery.getSessionId(request) match {
      case Some(sessionId) =>
        sessionService.getSessionById(GetSessionByIdRequest(sessionId)).map { response =>
          Results.Ok(Seq(
            response.toString,
            "Sessions: " + SessionRepository.values.toString
          ).mkString("\n\n"))
        }
      case None =>
        Future.successful(Results.Ok("No cookies"))
    }
  }

  def form(): Action[AnyContent] = Action { request =>
    Results.Ok(views.html.index("Title"))
  }
}

class LoginControllerImpl extends LoginController
  with MixInSessionService
  with MixInCookieBakery
  with MixInDefaultExecutionContext

trait UsesCookieBakery {
  def cookieBakery: CookieBakery
}

trait MixInCookieBakery {
  val cookieBakery = new CookieBakery
}

class CookieBakery {

  val KEY_SESSION = "session"

  /**
    * Adds session ID cookie.
    */
  def attachSession(result: Result, session: Session): Result = {
    val cookie = Cookie(KEY_SESSION, session.id.toString)
    // TODO: set maxAge
    result.withCookies(cookie)
  }

  def getSessionId(request: Request[_]): Option[Long] = {
    request.cookies.get(KEY_SESSION).flatMap(v => Try(v.value.toLong).toOption)
  }
}
