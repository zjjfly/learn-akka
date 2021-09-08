package com.github.zjjfly.server

import akka.actor._

class ScalaPongActor(response: String) extends Actor with ActorLogging {
  override def receive: Receive = {
    case "Ping" =>
      sender() ! response
    case _ =>
      sender() ! Status.Failure(new Exception("unknown message"))
  }
}

object ScalaPongActor {
  def props(response: String): Props = {
    Props(new ScalaPongActor(response))
  }
}

object CreateActor extends App {
  val system = ActorSystem()
  //Akka不直接操作Actor,而是通过ActorRef.
  val actorRef: ActorRef = system.actorOf(ScalaPongActor props "Pong!")
  println(actorRef.path) //Actor的路径
  //知道了Actor的路径,就可以使用actorSelection得到一个ActorSelection,它和ActorRef类似,是一个Actor的引用
  //val actorSelection: ActorSelection = system.actorSelection(actorRef.path)
}
