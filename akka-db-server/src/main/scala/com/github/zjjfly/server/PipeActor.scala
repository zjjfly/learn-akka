package com.github.zjjfly.server

import akka.actor._
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

//pipe用于把future的结果返回给sender().如果使用future.map(x=> send())会有问题
//因为future的回调函数是在其他线程执行的,在那里运行sender()的返回的不一定是正确的值
class PipeActor(target: ActorRef) extends Actor with ActorLogging {
  implicit val timeOut: Timeout = Timeout(5 seconds)

  override def receive: Receive = {
    case "Ping" =>
      val future: Future[Any] = target ? "Ping"
      pipe(future) to sender()
    case _ =>
      sender() ! Status.Failure(new Exception("unknown message"))
  }
}

object PipeActor {
  def props(target: ActorRef): Props = {
    Props(new PipeActor(target))
  }
}
