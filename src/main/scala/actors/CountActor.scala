package actors

import cats.effect.{IO, Ref}
import com.suprnation.actor.ReplyingActor

sealed trait CountRequest
case object Increment extends CountRequest
case object GetCount extends CountRequest

case class CountActor(state: Ref[IO, Int])
    extends ReplyingActor[IO, CountRequest, String]:

  override def receive = {
    case Increment =>
      for
        _ <- IO.println("Incrementing count...")
        _ <- state.update(_ + 1)
      yield ""
    case GetCount =>
      for
        _ <- IO.println(s"Getting count... from ${context.sender}")
        count <- state.get
        reply = s"count=$count"
        _ <- IO.println(reply)
      yield reply
  }

object CountActor:
  def apply(): IO[CountActor] = Ref.of[IO, Int](0).map(new CountActor(_))
