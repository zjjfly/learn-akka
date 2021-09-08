package com.github.zjjfly.server

import akka.testkit.TestActorRef
import com.github.zjjfly.db.message.SetRequest

class AkkaDbTest extends BasicActorTest {
  describe("akka DB") {
    describe("given SetRequest") {
      it("should place key/value into map") {
        val actorRef = TestActorRef(new AkkaDB)
        actorRef ! SetRequest("name", "jjzi")
        val akkaDb = actorRef.underlyingActor
        akkaDb.map.get("name") should equal(Some("jjzi"))
      }
    }
  }
}
