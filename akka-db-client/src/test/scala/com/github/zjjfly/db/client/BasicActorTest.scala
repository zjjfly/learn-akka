package com.github.zjjfly.db.client

import akka.actor.ActorSystem
import akka.util.Timeout
import org.scalatest.{FunSpecLike, Matchers}
import scala.concurrent.duration._

/**
  * @author zjjfly
  */
trait BasicActorTest extends FunSpecLike with Matchers {
  implicit val system: ActorSystem = ActorSystem()
  implicit val timeOut: Timeout = Timeout(5 seconds)
  val remoteAddress = "127.0.0.1:9000"
}
