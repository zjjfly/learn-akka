package com.github.zjjfly.supervision

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, SupervisorStrategy}
import com.github.zjjfly.supervision.exception.{
  BrokenPlateException,
  DrunkenFoolException,
  RestaurantFireError,
  TiredChefException
}

import scala.concurrent.duration._

class RestaurantManager extends Actor with ActorLogging {
  override def receive: Receive = ???

  override def supervisorStrategy: SupervisorStrategy = {
    //maxNrOfRetries设置在抛出异常之前重新尝试发送信息的次数
    //withinTimeRange设置超出异常之前重新尝试发送的超时时间,过了这个超时时间就不再尝试重新发送失败的信息了
    OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 3.minutes) {
      case _: BrokenPlateException => Resume
      case _: DrunkenFoolException => Restart
      case _: RestaurantFireError  => Escalate //向上一级actor反映
      case _: TiredChefException   => Stop
      case _                       => Escalate
    }
  }

  //preRestart和postRestart只在重启的时候调用,只要重启就会各调用一次,而preStart和postStop在Actor的生命周期中调用一次
  //默认的preRestart实现会调用postStop,默认的postRestart实现会调用preStart
  override def preRestart(reason: Throwable, message: Option[Any]): Unit =
    super
      .preRestart(reason, message)

  override def postRestart(reason: Throwable): Unit = super.postRestart(reason)
}
