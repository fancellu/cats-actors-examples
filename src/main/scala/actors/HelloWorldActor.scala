package actors

import cats.effect.IO
import com.suprnation.actor.Actor.{Actor, Receive}
import com.suprnation.actor.ReplyingActor

sealed trait Request
case class SayHello(name: String) extends Request
case object Terminate extends Request
case object Stop extends Request

case class HelloWorldActor() extends Actor[IO, Request]:

  override def receive: Receive[IO, Request] = {
    case Terminate =>
      IO.println("Terminating actor system") >> context.system.terminate()
    case Stop =>
      IO.println("Stopping actor...") >> context.self.stop
    case SayHello(name) =>
      IO.println(s"Hello, $name")
  }
