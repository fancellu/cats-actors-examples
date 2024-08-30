import actors.*
import cats.effect.{IO, IOApp, Resource}
import cats.implicits.*
import com.suprnation.actor.ActorRef.ActorRef
import com.suprnation.actor.{ActorSystem, Kill, PoisonPill}

import scala.concurrent.duration.*

object ActorsApp extends IOApp.Simple:

  // waits for user input and sends to helloWorldActorRef
  private def repl(actorRef: ActorRef[IO, Request]): IO[Unit] =

    val handleRight: String => IO[Unit] = {
      case "TERMINATE"  => actorRef ! Terminate
      case "STOP"       => actorRef ! Stop
      case "KILL"       => actorRef !* Kill
      case "POISONPILL" => actorRef !* PoisonPill
      case msg          => actorRef ! SayHello(msg)
    }
    val handleLeft: Throwable => IO[Unit] = _ => IO.unit

    for
      input <- IO.print(">>> ") >> IO.readLine.attempt
      _ <- input.fold(handleLeft, handleRight.map(_ >> repl(actorRef)))
    yield ()

  private def sendAfterDelay(actorRef: ActorRef[IO, Request]): IO[Unit] =
    IO.sleep(2.second) >> (actorRef ! SayHello("once"))

  // sends messages to helloWorldActorRef every 8 seconds
  private def sendMessagesForever(actorRef: ActorRef[IO, Request]): IO[Unit] =
    val messages = List("One", "Two", "three", "Four")
    val sendMessage =
      messages.traverse_(msg =>
        (actorRef ! SayHello(msg)) >> IO.sleep(8.second)
      )
    sendMessage.foreverM

  override def run: IO[Unit] =
    val actorSystemResource: Resource[IO, ActorSystem[IO]] =
      ActorSystem[IO]("ActorSystem")

    actorSystemResource.use { system =>
      for
        helloWorldActorRef <- system.actorOf(
          HelloWorldActor(),
          "helloWorldActor"
        )
        // spin up fibers and sends them HelloWorldActorRef
        _ <- List(sendMessagesForever, repl, sendAfterDelay).traverse_(
          _.apply(helloWorldActorRef).start
        )
        // reply example
        echoActorRef <- system.replyingActorOf(
          EchoActor(),
          "echoActor"
        )

        response <- echoActorRef ? "echo this"
        _ <- IO.println(s"Received response: $response")
        // wait for system to terminate, e.g. if you enter TERMINATE on repl

        countActorRef <- system.replyingActorOf(
          CountActor(),
          "countActor"
        )
        // fire and forget a few times
        _ <- countActorRef ! Increment
        _ <- countActorRef ! Increment
        _ <- countActorRef ! GetCount
        _ <- countActorRef ! Increment
        // wait for reply
        reply <- countActorRef ? GetCount
        _ <- IO.println(s"Received reply: $reply")
        _ <- system.waitForTermination
      yield ()
    }
