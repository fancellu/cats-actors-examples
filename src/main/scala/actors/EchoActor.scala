package actors

import cats.effect.IO
import com.suprnation.actor.ReplyingActor

class EchoActor extends ReplyingActor[IO, String, String]:
  override def receive = { case name =>
    IO.println(s"Echoing $name").as(name + "!!!!!")
  }
