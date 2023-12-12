package ru.tinkoff.newsaggregator.repository.postgresql

import cats.Applicative
import cats.effect.kernel.MonadCancelThrow
import doobie.Transactor
import doobie.implicits._
import io.getquill.SnakeCase
import io.getquill.doobie.DoobieContext
import ru.tinkoff.newsaggregator.repository.UserRepository

class UserRepositoryPostgresql[F[_]: Applicative: MonadCancelThrow](implicit tr: Transactor[F])
    extends UserRepository[F] {

  private val ctx = new DoobieContext.Postgres(SnakeCase)
  import ctx.{SqlInfixInterpolator => _, _}
  override def isExists(name: String, password: String): F[Option[Boolean]] =
    (for {
      result <-
        sql"""
             SELECT compare_password($name, $password);
             """
          .query[Boolean]
          .option
    } yield result)
      .transact(tr)
}
