package ru.tinkoff.newsaggregator.repository.postgresql

import cats.effect.kernel.MonadCancelThrow
import cats.implicits.toFunctorOps
import doobie.Transactor
import doobie.implicits._
import io.getquill.SnakeCase
import io.getquill.doobie.DoobieContext
import ru.tinkoff.newsaggregator.domain.order.Order
import ru.tinkoff.newsaggregator.repository.OrderRepository

import java.util.UUID

class OrderRepositoryPostgresql[F[_]: MonadCancelThrow](implicit tr: Transactor[F])
    extends OrderRepository[F] {

  private val ctx = new DoobieContext.Postgres(SnakeCase)
  import ctx._

  override def create(order: Order): F[Long] = run {
    quote {
      querySchema[Order]("\"order\"").insertValue(lift(order))
    }
  }.transact(tr)

  override def list: F[List[Order]] = run {
    quote {
      querySchema[Order]("\"order\"")
    }
  }.transact(tr)

  override def get(id: UUID): F[Option[Order]] = run {
    quote {
      querySchema[Order]("\"order\"").filter(_.id == lift(id))
    }
  }.transact(tr).map(_.headOption)

  override def delete(id: UUID): F[Option[Order]] = run {
    quote {
      querySchema[Order]("\"order\"").filter(_.id == lift(id)).delete.returningMany(r => r)
    }
  }.transact(tr).map(_.headOption)
}
