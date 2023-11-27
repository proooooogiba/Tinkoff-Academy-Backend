package ru.tinkoff.petstore.service

import cats.FlatMap
import cats.effect.kernel.Clock
import cats.effect.std.UUIDGen
import ru.tinkoff.petstore.repository.OrderRepository
import cats.syntax.functor._
import cats.syntax.flatMap._
import ru.tinkoff.petstore.domain.order.{CreateOrder, Order, OrderResponse}

import java.util.UUID

trait OrderService[F[_]] {
  def create(createOrder: CreateOrder): F[OrderResponse]

  def list: F[List[OrderResponse]]

  def get(id: UUID): F[Option[OrderResponse]]

  def delete(id: UUID): F[Option[OrderResponse]]
}

object OrderService {
  private class Impl[F[_]: UUIDGen: FlatMap: Clock](orderRepository: OrderRepository[F])
      extends OrderService[F] {
    def create(createOrder: CreateOrder): F[OrderResponse] =
      for {
        id <- UUIDGen[F].randomUUID
        now <- Clock[F].realTimeInstant
        order = Order.fromCreateOrder(id, now, createOrder)
        _ <- orderRepository.create(order)
      } yield order.toResponse

    override def list: F[List[OrderResponse]] =
      orderRepository.list
        .map(_.map(_.toResponse))

    override def get(id: UUID): F[Option[OrderResponse]] =
      orderRepository
        .get(id)
        .map(_.map(_.toResponse))

    override def delete(id: UUID): F[Option[OrderResponse]] =
      orderRepository
        .delete(id)
        .map(_.map(_.toResponse))
  }

  def make[F[_]: UUIDGen: FlatMap: Clock](orderRepository: OrderRepository[F]): OrderService[F] =
    new Impl[F](orderRepository)
}
