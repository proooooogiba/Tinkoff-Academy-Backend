package ru.tinkoff.newsaggregator.repository.inmemory

import cats.Functor
import cats.implicits.toFunctorOps
import ru.tinkoff.newsaggregator.common.cache.Cache
import ru.tinkoff.newsaggregator.domain.news.News
import ru.tinkoff.newsaggregator.repository.NewsRepository

import java.time.ZonedDateTime
import java.util.UUID

class NewsRepositoryInMemory[F[_]: Functor](cache: Cache[F, UUID, News]) extends NewsRepository[F] {
  override def create(createNews: News): F[Long] =
    cache
      .add(createNews.id, createNews)
      .as(1L)

  override def list: F[List[News]] = cache.values

  override def get(id: UUID): F[Option[News]] = cache.remove(id)

  override def delete(id: UUID): F[Option[News]] = cache.remove(id)

  override def getByKeyWord(keyWord: String): F[List[News]] =
    cache.values.map(_.filter(_.content.contains(keyWord)))

  override def listByDate(start: ZonedDateTime, end: ZonedDateTime): F[List[News]] =
    cache.values.map(
      _.filter(news => news.publishedAt.isAfter(start) && news.publishedAt.isBefore(end)),
    )
}

object NewsRepositoryInMemory {
  def apply[F[_]: Functor](cache: Cache[F, UUID, News]) =
    new NewsRepositoryInMemory[F](cache)
}
