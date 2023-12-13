package ru.tinkoff.newsaggregator.repository.postgresql

import cats.effect.kernel.MonadCancelThrow
import cats.implicits.toFunctorOps
import doobie.Transactor
import doobie.implicits._
import io.getquill.SnakeCase
import io.getquill.doobie.DoobieContext
import ru.tinkoff.newsaggregator.domain.news.News
import ru.tinkoff.newsaggregator.repository.NewsRepository

import java.time.ZonedDateTime
import java.util.UUID
import scala.language.postfixOps

class NewsRepositoryPostgresql[F[_]: MonadCancelThrow](implicit tr: Transactor[F])
    extends NewsRepository[F] {

  private val ctx = new DoobieContext.Postgres(SnakeCase)
  import ctx.{SqlInfixInterpolator => _, _}
  import doobie.postgres._
  import doobie.postgres.implicits._

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

  override def getByKeyWord(keyWord: String): F[List[News]] =
    (for {
      result <-
        sql"""
             SELECT * FROM news
             WHERE to_tsvector('english', content) @@ to_tsquery('english', $keyWord);
             """
          .query[News]
          .to[List]
    } yield result)
      .transact(tr)

  override def listByDate(start: ZonedDateTime, end: ZonedDateTime): F[List[News]] = {
    (for {
      result <-
        sql"""
             SELECT *
             FROM "news" n
             WHERE n.published_at > $start AND n.published_at < $end;
             """
          .query[News]
          .to[List]
    } yield result)
      .transact(tr)
  }
}
