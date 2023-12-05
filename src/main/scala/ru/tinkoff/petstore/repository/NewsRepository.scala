package ru.tinkoff.petstore.repository

import ru.tinkoff.petstore.domain.news.News

import java.util.UUID

trait NewsRepository[F[_]] {
  def create(createNews: News): F[Long]
  def list: F[List[News]]

  def get(id: UUID): F[Option[News]]

  def delete(id: UUID): F[Option[News]]
}
