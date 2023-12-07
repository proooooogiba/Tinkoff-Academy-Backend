package ru.tinkoff.newsaggregator.database

import cats.effect.Sync
import cats.syntax.functor._
import org.flywaydb.core.Flyway
import ru.tinkoff.newsaggregator.config.PostgresConfig

object FlywayMigration {

  private def loadFlyway(config: PostgresConfig): Flyway =
    Flyway
      .configure()
      .locations("db.migration")
      .cleanDisabled(false)
      .dataSource(config.url, config.user, config.password)
      .load()

  def migrate[F[_]](config: PostgresConfig)(implicit F: Sync[F]): F[Unit] =
    F.delay(loadFlyway(config).migrate()).void

  def clean[F[_]](config: PostgresConfig)(implicit F: Sync[F]): F[Unit] =
    F.delay(loadFlyway(config).clean()).void
}
