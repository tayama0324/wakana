package test

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import play.api.Play.current
import play.api.mvc.Controller
import play.api.mvc.WebSocket

/**
 * Created by takashi_tayama on 2016/01/10.
 */
object WebSocketController extends Controller {

  def socket() = WebSocket.acceptWithActor[String, String] { request => out =>
    props(out)
  }

  def props(out: ActorRef) = Props(new ByteArrayStoreActor(out))
}

class ByteArrayStoreActor(out: ActorRef) extends Actor {
  def receive: Receive = {
    case msg: String =>
      println("Received " + msg)
      out ! ("Received " + msg)
  }
}
