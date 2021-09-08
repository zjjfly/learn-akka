package com.github.zjjfly.db.client

import akka.actor.ActorSystem
import akka.pattern._
import akka.util.Timeout
import com.github.zjjfly.db.message.{DeleteRequest, GetRequest, SetIfNotExist, SetRequest}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class SClient(remoteAddress: String, timeOutSeconds: Int) {
  private implicit val timeOut = Timeout(timeOutSeconds seconds)
  private implicit val system = ActorSystem("LocalSystem")
  private val remoteDb =
    system.actorSelection(s"akka.tcp://akkadb@$remoteAddress/user/akkadb")

  def set(key: String, value: Object): Future[Any] = {
    remoteDb ? SetRequest(key, value)
  }

  def get(key: String): Future[Any] = {
    remoteDb ? GetRequest(key)
  }

  def setIfExist(key: String, value: Object): Future[Any] = {
    remoteDb ? SetIfNotExist(key, value)
  }

  def delete(key: String): Future[Any] = {
    remoteDb ? DeleteRequest(key)
  }
}
