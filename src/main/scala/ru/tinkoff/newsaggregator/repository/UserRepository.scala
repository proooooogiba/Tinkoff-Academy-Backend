package ru.tinkoff.newsaggregator.repository

trait UserRepository[F[_]] {
  def isExists(name: String, password: String): F[Option[Boolean]]
}
