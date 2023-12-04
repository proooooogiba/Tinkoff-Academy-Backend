package ru.tinkoff.petstore.repository

import ru.tinkoff.petstore.domain.order.Order

import java.util.UUID

trait NewsRepository[F[_]] {
  def create(order: Order): F[Long]

  def list: F[List[Order]]

  def get(id: UUID): F[Option[Order]]

  def delete(id: UUID): F[Option[Order]]
}
