package com.github.zjjfly.server

import akka.actor.{Actor, ActorLogging, Props, Status}
import com.github.zjjfly.db.exception.KeyNotFoundException
import com.github.zjjfly.db.message.{
  DeleteRequest,
  GetRequest,
  SetIfNotExist,
  SetRequest
}

import scala.collection.mutable

class AkkaDB extends Actor with ActorLogging {
  val map = new mutable.HashMap[String, Object]()

  override def preStart(): Unit = log.info("akka db start up!")

  override def postStop(): Unit = log.info("akka db shutdown!")

  override def receive: Receive = {
    case SetRequest(key, value) =>
      log.info("receive SetRequest - key:{},value:{}", key, value)
      map.put(key, value)
      sender() ! Status.Success
    case SetIfNotExist(key, value) =>
      log.info("receive SetIfExist - key:{},value:{}", key, value)
      if (!map.contains(key)) map.put(key, value)
      sender() ! Status.Success
    case GetRequest(key) =>
      log.info("receive GetRequest - key:{}", key)
      val response: Option[Object] = map.get(key)
      response match {
        case Some(x) => sender() ! x
        case None    => sender() ! Status.Failure(KeyNotFoundException(key))
      }
    case DeleteRequest(key) =>
      log.info("receive DeleteRequest - key:{}", key)
      val response = map.remove(key)
      response match {
        case Some(_) => sender() ! Status.Success
        case None    => sender() ! Status.Failure(KeyNotFoundException(key))
      }
    case o =>
      log.info("receive unknown message: {}", o)
      sender() ! Status.Failure(new ClassNotFoundException)
  }
}

object AkkaDB {
  def props(): Props = {
    Props(new AkkaDB)
  }
}
