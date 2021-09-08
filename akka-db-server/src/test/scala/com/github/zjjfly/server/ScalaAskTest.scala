package com.github.zjjfly.server

import akka.testkit.TestActorRef
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}
import akka.pattern._

class ScalaAskTest extends BasicActorTest {
  val pongActor = TestActorRef(new ScalaPongActor("Pong!"))
  describe("Pong actor") {
    it("should respond with 'Pong'") {
      val future = pongActor ? "Ping"
      val result = Await.result(future.mapTo[String], 1 seconds)
      assert(result == "Pong!")
    }
    it("should failed on unknown message") {
      val future: Future[Any] = pongActor ? "unknown"
      //intercept用于断言抛出一个特定类型的异常
      intercept[Exception] {
        //这里使用一个阻塞方法来得到Future的结果,这是为了测试,生产环境不要这么做
        Await.result(future.mapTo[String], 1 seconds) //需要调用mapTo[T]把Future[Any]转成Future[T]
      }
    }
  }

  def askPong(message: String): Future[String] = {
    (pongActor ? message).mapTo[String]
  }

  val askHandler: PartialFunction[Try[String], Unit] = {
    case Success(x) => println("replied with: " + x)
    case Failure(exception) =>
      println(s"replied with error message:$exception") //处理异常情况
  }

  //测试多线程的异步操作,需要sleep一段时间,不然测试会马上结束,导致异步线程没有运行结束
  describe("test real asynchronous") {
    import scala.concurrent.ExecutionContext.Implicits.global
    it("should print to console") {
      askPong("Ping").onComplete(askHandler)
      //链式异步操作
      askPong("Ping")
        .flatMap(askPong)
        .onComplete(askHandler)
      //失败时恢复
      askPong("Pong")
        .recover {
          case _: Exception => "default"
        }
        .onComplete(askHandler)
      //失败时使用另一个异步来恢复重试,这种情况很常用
      askPong("Pong")
        .recoverWith {
          case _: Exception => askPong("Ping")
        }
        .onComplete(askHandler)
      Thread.sleep(1000)
    }
  }
}
