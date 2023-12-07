package ru.tinkoff.newsaggregator.service

import cats.FlatMap
import cats.effect.std.UUIDGen
import cats.syntax.flatMap._
import cats.syntax.functor._
import ru.tinkoff.newsaggregator.api.client.retrying.RetryingNewsClient
import ru.tinkoff.newsaggregator.domain.news.request.CreateNewsRequest
import ru.tinkoff.newsaggregator.domain.news.response.NewsAPIResponse
import ru.tinkoff.newsaggregator.domain.news.{News, NewsCategory, NewsCountry, NewsResponse}
import ru.tinkoff.newsaggregator.repository.NewsRepository

import java.time.ZonedDateTime
import java.util.UUID

trait NewsService[F[_]] {
  def getByKeyWord(keyWord: String): F[Option[NewsAPIResponse]]

  def getHeadlinesByCategory(category: NewsCategory): F[Option[NewsAPIResponse]]

  def getHeadlinesByCountry(country: NewsCountry): F[Option[NewsAPIResponse]]

  def create(createNews: CreateNewsRequest): F[NewsResponse]

  def list: F[List[NewsResponse]]

  def get(id: UUID): F[Option[NewsResponse]]

  def delete(id: UUID): F[Option[NewsResponse]]

  def getByKeyWordFromDB(keyWord: String): F[List[NewsResponse]]

  def listByDate(start: ZonedDateTime, end: ZonedDateTime): F[List[NewsResponse]]
}

object NewsService {
  private class Impl[F[_]: UUIDGen: FlatMap](
      newsRepository: NewsRepository[F],
      newsClient: RetryingNewsClient[F],
  ) extends NewsService[F] {
    override def getByKeyWord(keyWord: String): F[Option[NewsAPIResponse]] =
      newsClient.getByKeyWord(keyWord)

    override def getHeadlinesByCategory(category: NewsCategory): F[Option[NewsAPIResponse]] =
      newsClient.getHeadlinesByCategory(category)

    override def getHeadlinesByCountry(country: NewsCountry): F[Option[NewsAPIResponse]] =
      newsClient.getHeadlinesByCountry(country)

    override def create(createNews: CreateNewsRequest): F[NewsResponse] =
      for {
        id <- UUIDGen[F].randomUUID
        news = News.fromCreateNews(id, createNews)
        _ <- newsRepository.create(news)
      } yield news.toResponse

    override def list: F[List[NewsResponse]] =
      newsRepository.list
        .map(_.map(_.toResponse))

    override def get(id: UUID): F[Option[NewsResponse]] =
      newsRepository
        .get(id)
        .map(_.map(_.toResponse))

    override def delete(id: UUID): F[Option[NewsResponse]] =
      newsRepository
        .delete(id)
        .map(_.map(_.toResponse))

    override def listByDate(start: ZonedDateTime, end: ZonedDateTime): F[List[NewsResponse]] =
      newsRepository.listByDate(start, end).map(_.map(_.toResponse))

    override def getByKeyWordFromDB(keyWord: String): F[List[NewsResponse]] =
      newsRepository
        .getByKeyWord(keyWord)
        .map(_.map(_.toResponse))

  }

  def make[F[_]: UUIDGen: FlatMap](
      newsRepository: NewsRepository[F],
      newsClient: RetryingNewsClient[F],
  ): NewsService[F] =
    new Impl[F](newsRepository, newsClient)
}
