package com.github.zjjfly.rss

import java.util.concurrent.TimeoutException

import akka.actor.Status.Failure
import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import com.github.zjjfly.db.message.{
  ArticleBody,
  GetRequest,
  HttpResponse,
  ParseArticle,
  ParseHtmlArticle,
  SetRequest
}

/**
  * 相对于Ask,使用Tell的性能更好,因为每个Ask会产生一个Future和一个临时的Actor
  * 而且使用Ask对于每个Ask要指定超时时间,当接受Ask请求的Actor需要向另一个Actor发送Ask请求时,超时时间的选取会很麻烦
  * @param cacheActorPath
  * @param httpClientActorPath
  * @param articleParserActorPath
  * @param timeOut
  */
class TellArticleParse(cacheActorPath: String,
                       httpClientActorPath: String,
                       articleParserActorPath: String,
                       implicit val timeOut: Timeout)
    extends Actor {
  import scala.concurrent.ExecutionContext.Implicits.global

  val cacheActor = context.actorSelection(cacheActorPath)
  val httpClientActor = context.actorSelection(httpClientActorPath)
  val articleParserActor = context.actorSelection(articleParserActorPath)

  private def buildExtraActor(senderRef: ActorRef, uri: String) = {
    context.actorOf(Props(new Actor {
      override def receive: Receive = {
        case "timeout" =>
          senderRef ! Failure(new TimeoutException("timeout!"))
          context.stop(self)
        case HttpResponse(body) =>
          articleParserActor ! ParseHtmlArticle(uri, body)
        case body: String =>
          senderRef ! body
          context.stop(self)
        case ArticleBody(uri, body) =>
          cacheActor ! SetRequest(uri, body)
          senderRef ! body
          context.stop(self)
        case t =>
          println("ignoring msg: " + t.getClass)
      }
    }))
  }

  override def receive: Receive = {
    case ParseArticle(uri) =>
      val extraActor = buildExtraActor(sender(), uri)
      cacheActor ! (GetRequest(uri), extraActor)
      httpClientActor ! ("test", extraActor)
      context.system.scheduler
        .scheduleOnce(timeOut.duration, extraActor, "timeout")
  }
}
