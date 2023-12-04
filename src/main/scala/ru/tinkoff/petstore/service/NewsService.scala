package ru.tinkoff.petstore.service

import cats.FlatMap
import cats.effect.std.UUIDGen
import ru.tinkoff.petstore.api.news.model.response.NewsResponse
import ru.tinkoff.petstore.api.service.RetryingNewsClient
import ru.tinkoff.petstore.domain.news.{NewsCategory, NewsCountry}
import ru.tinkoff.petstore.repository.NewsRepository

trait NewsService[F[_]] {
  //  функционал:
  //  поиск по ключевым словам -
  //  поиск по категориям
  //  лента новостей -
  // сохранённые новости и их лента - тут как раз таки будет реализован CRUD - типо есть news, а есть my news
  // возможность поделиться новостями - сервак должен будет предоставлять ссылку на новость в json-формате

  def getByKeyWord(keyWord: String): F[Option[NewsResponse]]

  def getHeadlinesByCategory(category: NewsCategory): F[Option[NewsResponse]]

  def getHeadlinesByCountry(country: NewsCountry): F[Option[NewsResponse]]
}

object NewsService {

  private class Impl[F[_]](newsRepository: NewsRepository[F], newsClient: RetryingNewsClient[F])
      extends NewsService[F] {
    override def getByKeyWord(keyWord: String): F[Option[NewsResponse]] =
      newsClient.getByKeyWord(keyWord)

    override def getHeadlinesByCategory(category: NewsCategory): F[Option[NewsResponse]] =
      newsClient.getHeadlinesByCategory(category)

    override def getHeadlinesByCountry(country: NewsCountry): F[Option[NewsResponse]] =
      newsClient.getHeadlinesByCountry(country)
  }

  def make[F[_]: UUIDGen: FlatMap](
      newsRepository: NewsRepository[F],
      newsClient: RetryingNewsClient[F],
  ): NewsService[F] =
    new Impl[F](newsRepository, newsClient)
}
