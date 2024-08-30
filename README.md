# Cats Effect Actors examples with cats-actors

Spins up 3 Actors

## HelloWorldActor

A non replying actor, taking custom commands

## EchoActor

A replying actor, echoing back incoming string

## CountActor

A replying actors with Int state, that takes Increment GetCount commands

## Extra functionality

Spins up some fibers and points to HelloWorldActor

- sendMessagesForever()
  - Sends strings "One", "Two", "three", "Four" via SayHello every 8 seconds, forever
- repl()
  - Handles user input on console, sends to HelloWorldActor
    - "TERMINATE" terminates entire Actor System
    - "STOP" stops Actor
    - "KILL" kills Actor
    - "POISONPILL" poisons Actor
    - Anything else just gets sent to SayHello
- sendAfterDelay()
  - Sends SayHello after 2 seconds

Upon startup sends some messages to EchoActor and CountActor

