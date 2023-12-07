package ru.tinkoff.newsaggregator.pets

import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

// FunSuite testing style
class OrderServiceSuite extends AnyFunSuite with Matchers with MockFactory {

//  test("OrderService should create order with actual time") {
//
//    val time = Instant.now()
//
//    implicit val clock: Clock = mock[Clock]
//    (clock.instant _).expects().returning(time)
//
//    val repository = OrderRepositoryInMemory()
//    val service = new OrderService(repository)
//    val order = service.buyPet(1)
//
//    order shouldBe Order(1, time)
//  }

}
