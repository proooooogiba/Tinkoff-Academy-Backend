package ru.tinkoff.petstore.service

import cats.FlatMap
import cats.effect.std.UUIDGen
import cats.syntax.flatMap._
import cats.syntax.functor._
import ru.tinkoff.petstore.api.service.RetryingNewsClient
import ru.tinkoff.petstore.domain.news.request.CreateNewsRequest
import ru.tinkoff.petstore.domain.news.response.NewsAPIResponse
import ru.tinkoff.petstore.domain.news.{News, NewsCategory, NewsCountry, NewsResponse}
import ru.tinkoff.petstore.repository.NewsRepository

trait NewsService[F[_]] {
  def getByKeyWord(keyWord: String): F[Option[NewsAPIResponse]]

  def getHeadlinesByCategory(category: NewsCategory): F[Option[NewsAPIResponse]]

  def getHeadlinesByCountry(country: NewsCountry): F[Option[NewsAPIResponse]]
  def create(createNews: CreateNewsRequest): F[NewsResponse]
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

//    override def list: F[List[News]] = ???
//
//    override def get(id: UUID): F[Option[News]] = ???
//
//    override def delete(id: UUID): F[Option[News]] = ???

  }

  def make[F[_]: UUIDGen: FlatMap](
      newsRepository: NewsRepository[F],
      newsClient: RetryingNewsClient[F],
  ): NewsService[F] =
    new Impl[F](newsRepository, newsClient)
}
