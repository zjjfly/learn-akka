package com.github.zjjfly.server

import akka.actor.ActorSystem
import akka.util.Timeout
import org.scalatest.{FunSpecLike, Matchers}

trait BasicActorTest extends FunSpecLike with Matchers {
  import scala.concurrent.duration._
  implicit val system: ActorSystem = ActorSystem()
  implicit val timeOut: Timeout = Timeout(5 seconds)
}
