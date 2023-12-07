package ru.tinkoff.newsaggregator.repository

import ru.tinkoff.newsaggregator.domain.news.News

import java.time.ZonedDateTime
import java.util.UUID

trait NewsRepository[F[_]] {
  def create(createNews: News): F[Long]
  def list: F[List[News]]

  def get(id: UUID): F[Option[News]]

  def delete(id: UUID): F[Option[News]]

  def getByKeyWord(keyWord: String): F[List[News]]

  def listByDate(start: ZonedDateTime, end: ZonedDateTime): F[List[News]]
}
