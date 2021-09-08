package com.github.zjjfly.server

import akka.actor._

class TerminateActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case "Ping" =>
      sender() ! "Pong"
      log.info("finish handling terminate request")
    case "Kill You" =>
      log.info("begin to handle kill message")
      Thread.sleep(1000)
      sender() ! "kill you response"
    case _ =>
      sender() ! Status.Failure(new Exception("unknown message"))
  }
}

object TerminateActor {
  def props(): Props = {
    Props(new TerminateActor)
  }
}
