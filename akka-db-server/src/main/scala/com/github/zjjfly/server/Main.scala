package com.github.zjjfly.server

import akka.actor.{ActorRef, ActorSystem}

object Main extends App {
  val system = ActorSystem("akkadb")
  //参数是系统名,远程地址中会出现在@之前
  system.actorOf(AkkaDB.props(), "akkadb") //参数是actor名,远程地址中会出现在user之后
  val pongActor: ActorRef =
    system.actorOf(ScalaPongActor.props("Pong!"), "ping-pong")
  system.actorOf(ForwardActor.props(pongActor), "forward")
  system.actorOf(PipeActor.props(pongActor), "pipe")
  val terminate: ActorRef = system.actorOf(TerminateActor.props(), "terminate")
  system.actorOf(SupervisorActor.props(terminate), "supervisor")
}
