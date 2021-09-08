package com.github.zjjfly.db.client

import com.github.zjjfly.db.exception.KeyNotFoundException
import org.scalatest.{FunSpecLike, Matchers}

import scala.concurrent.Await

class SClientIntegrationSpec extends FunSpecLike with Matchers {
  import scala.concurrent.duration._
  val client = new SClient("127.0.0.1:9000", 2)
  describe("akka db client test") {
    it("should set a value") {
      client.set("name", "jjzi")
      val future = client.get("name")
      val result = Await.result(future, 10 seconds)
      result should equal("jjzi")
    }
    it("should respond with KeyNotFoundException") {
      intercept[KeyNotFoundException] {
        Await.result(client.get("age"), 10 seconds)
      }
    }
    it("should no set a value if key exist") {
      client.setIfExist("name", "zjj")
      val future = client.get("name")
      val result = Await.result(future, 10 seconds)
      result should equal("jjzi")
    }
    it("should delete a key/value") {
      client.delete("name")
      intercept[KeyNotFoundException] {
        Await.result(client.get("name"), 10 seconds)
      }
    }
  }
}
