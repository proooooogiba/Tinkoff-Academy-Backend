package ru.tinkoff.petstore.repository.inmemory

import cats.Functor
import ru.tinkoff.petstore.domain.order.Order
import ru.tinkoff.petstore.repository.OrderRepository
import cats.syntax.functor._
import ru.tinkoff.petstore.common.cache.Cache

import java.util.UUID

class OrderRepositoryInMemory[F[_]: Functor](cache: Cache[F, UUID, Order])
    extends OrderRepository[F] {

  override def create(order: Order): F[Long] =
    cache
      .add(order.id, order)
      .as(1L)

  override def list: F[List[Order]] = cache.values

  override def get(id: UUID): F[Option[Order]] = cache.get(id)

  override def delete(id: UUID): F[Option[Order]] = cache.remove(id)
}

object OrderRepositoryInMemory {
  def apply[F[_]: Functor](cache: Cache[F, UUID, Order]) =
    new OrderRepositoryInMemory[F](cache)
}
