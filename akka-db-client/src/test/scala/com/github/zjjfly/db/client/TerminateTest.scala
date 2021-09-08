package com.github.zjjfly.db.client

import java.util.concurrent.TimeoutException

import akka.actor.ActorDSL._
import akka.actor.{ActorSelection, Kill, PoisonPill}

class TerminateTest extends BasicActorTest {
  val terminateActor: ActorSelection =
    system.actorSelection(s"akka.tcp://akkadb@$remoteAddress/user/terminate")
  implicit val i = inbox()

  describe("terminate method test") {
    it(
      "send PoisonPill after send a ping message,still can receive a pong response") {
      terminateActor ! "Ping"
      //PoisonPill会让actor在处理完当前的消息后停止
      terminateActor ! PoisonPill
      val result = i.receive()
      result should equal("Pong")
    }
    it("send message to actor again and actor has been terminated") {
      terminateActor ! "Ping"
      intercept[TimeoutException] {
        i.receive()
      }
    }
    it("after Kill sent,the actor stop") {
      terminateActor ! "Kill You"
      //Actor收到Kill之后会抛出一个ActorKillException,由它的监督者的策略来决定如何处理,默认是停止Actor,和PoisonPill是一样的
      terminateActor ! Kill
      i.receive()
      //之后再发消息过去,actor不再处理
      terminateActor ! "Ping"
      intercept[TimeoutException] {
        i.receive()
      }
    }
  }
}
