package com.github.zjjfly.server

import akka.actor._
//forward会把请求转发给另一个actor,并且保证sender就是原始的发送者
class ForwardActor(target: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case "Ping" =>
      target forward "Ping"
    case _ =>
      sender() ! Status.Failure(new Exception("unknown message"))
  }
}

object ForwardActor {
  def props(target: ActorRef): Props = {
    Props(new ForwardActor(target))
  }
}
