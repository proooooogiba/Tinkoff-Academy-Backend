package ru.tinkoff.petstore.repository.postgresql

import cats.effect.kernel.MonadCancelThrow
import cats.implicits.toFunctorOps
import doobie.Transactor
import doobie.implicits._
import io.getquill.SnakeCase
import io.getquill.doobie.DoobieContext
import ru.tinkoff.petstore.domain.news.News
import ru.tinkoff.petstore.repository.NewsRepository

import java.util.UUID

class NewsRepositoryPostgresql[F[_]: MonadCancelThrow](implicit tr: Transactor[F])
    extends NewsRepository[F] {

  private val ctx = new DoobieContext.Postgres(SnakeCase)
  import ctx._

  override def create(news: News): F[Long] = run {
    quote {
      querySchema[News]("\"news\"").insertValue(lift(news))
    }
  }.transact(tr)

  override def list: F[List[News]] = run {
    quote {
      querySchema[News]("\"news\"")
    }
  }.transact(tr)

  override def get(id: UUID): F[Option[News]] = run {
    quote {
      querySchema[News]("\"news\"").filter(_.id == lift(id))
    }
  }.transact(tr).map(_.headOption)

  override def delete(id: UUID): F[Option[News]] = run {
    quote {
      querySchema[News]("\"news\"").filter(_.id == lift(id)).delete.returningMany(r => r)
    }
  }.transact(tr).map(_.headOption)
}
