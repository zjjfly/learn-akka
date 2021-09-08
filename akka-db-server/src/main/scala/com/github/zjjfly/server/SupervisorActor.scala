package com.github.zjjfly.server

import akka.actor._

class SupervisorActor(actorRef: ActorRef) extends Actor with ActorLogging {
  //watch方法用于注册需要被当前actor监督的actor,unwatch的效果则相反
  context.watch(actorRef)

  override def receive: Receive = {
    //子actor如果停止了,会给supervisor发送一个Terminated消息
    case Terminated(_) =>
      log.info("watching actor stopped")
    case _ =>
      log.info("unknown message")
  }
}

object SupervisorActor {
  def props(actorRef: ActorRef): Props = {
    Props(new SupervisorActor(actorRef))
  }
}
