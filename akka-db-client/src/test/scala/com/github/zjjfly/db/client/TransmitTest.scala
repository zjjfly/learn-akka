package com.github.zjjfly.db.client

import akka.actor.ActorDSL._
class TransmitTest extends BasicActorTest {
  val forwardActor =
    system.actorSelection(s"akka.tcp://akkadb@$remoteAddress/user/forward")
  val pipeActor =
    system.actorSelection(s"akka.tcp://akkadb@$remoteAddress/user/pipe")
  implicit val i = inbox()
  //inbox中有一个ActorRef,它会作为隐式参数传入!方法,所以返回的消息会被发到这个inbox中
  //inbox中有一个接受消息的队列,然后调用它的receive方法会返回队列中的第一个元素

  describe("transmission method test") {
    it("request should be forwarded to ping-pong actor") {
      forwardActor ! "Ping"
      val result = i.receive() //这个方法会阻塞当前线程
      result should equal("Pong!")
    }
    it("should pipe request into sender()") {
      pipeActor ! "Ping"
      val result = i.receive()
      result should equal("Pong!")
    }

  }
}
