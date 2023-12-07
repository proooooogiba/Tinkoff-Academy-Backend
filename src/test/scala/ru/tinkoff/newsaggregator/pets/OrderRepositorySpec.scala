package ru.tinkoff.newsaggregator.pets

import cats.implicits.catsSyntaxOptionId
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.newsaggregator.domain.order.Order
import ru.tinkoff.newsaggregator.repository.inmemory.OrderRepositoryInMemory

import java.time.Instant
import java.util.UUID

// FlatSpec testing style
class OrderRepositorySpec extends AnyFlatSpec with Matchers {

//  "OrderRepository" should "store orders" in {
//    val repository = OrderRepositoryInMemory()
//    val order = Order(UUID.randomUUID(), 1, Instant.now())
//    val createdOrder = repository.create(order)
//
//    createdOrder shouldBe order
//  }
//
//  it should "find orders" in {
//    val repository = OrderRepositoryInMemory()
//    val order = Order(UUID.randomUUID(), 1, Instant.now())
//    repository.create(order)
//
//    repository.getByPetId(1) shouldBe order.some
//  }

}
