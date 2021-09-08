package com.github.zjjfly.rss

import akka.actor.{Actor, Status}
import akka.util.Timeout
import com.github.zjjfly.db.message.{
  ArticleBody,
  GetRequest,
  HttpResponse,
  ParseArticle,
  ParseHtmlArticle,
  SetRequest
}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class AskArticleParser(cacheActorPath: String,
                       httpClientActorPath: String,
                       articleParserActorPath: String,
                       implicit val timeOut: Timeout)
    extends Actor {
  val cacheActor = context.actorSelection(cacheActorPath)
  val httpClientActor = context.actorSelection(httpClientActorPath)
  val articleParserActor = context.actorSelection(articleParserActorPath)
  import scala.concurrent.ExecutionContext.Implicits.global
  import akka.pattern._
  override def receive: Receive = {
    case ParseArticle(uri) =>
      val senderRef = sender()
      val cacheResult = cacheActor ? GetRequest(uri)
      val result = cacheResult.recoverWith {
        case _: Exception =>
          val fRawResult = httpClientActor ? uri
          fRawResult flatMap {
            case HttpResponse(rawContent) =>
              articleParserActor ? ParseHtmlArticle(uri, rawContent)
            case _ =>
              Future.failed(new Exception("unknown response"))
          }
      }
      result onComplete {
        case Success(x: String) =>
          println("cached result!")
          senderRef ! x
        case Success(x: ArticleBody) =>
          cacheActor ! SetRequest(uri, x.body)
          senderRef ! x
        case Failure(t) =>
          senderRef ! Status.Failure(t)
        case x =>
          println("unknown message! " + x)
      }
  }
}
