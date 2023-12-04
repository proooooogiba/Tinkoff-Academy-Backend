package ru.tinkoff.petstore.service

import cats.FlatMap
import cats.effect.std.UUIDGen
import ru.tinkoff.petstore.api.news.model.response.NewsResponse
import ru.tinkoff.petstore.api.service.RetryingNewsClient
import ru.tinkoff.petstore.repository.NewsRepository

trait NewsService[F[_]] {
  //  функционал:
  //  поиск по ключевым словам -
  //  поиск по категориям
  //  лента новостей -
  // сохранённые новости и их лента - тут как раз таки будет реализован CRUD - типо есть news, а есть my news
  // возможность поделиться новостями - сервак должен будет предоставлять ссылку на новость в json-формате

  def getByKeyWord(keyWord: String): F[Option[NewsResponse]]

  def getHeadlinesByCategory(category: Category): F[Option[NewsResponse]]
}

object NewsService {

  private class Impl[F[_]](newsRepository: NewsRepository[F], newsClient: RetryingNewsClient[F])
      extends NewsService[F] {
    override def getByKeyWord(keyWord: String): F[Option[NewsResponse]] =
      newsClient.getByKeyWord(keyWord)

    //    Option(System.getenv(NewsApiKeyEnv)) match {
//      case Some(apiKey) =>
//        val client = NewsApiClient(apiKey)
//        val Right(response) = client.topHeadlines(country = Some(CountryCode.US))
//        println(s"Found ${response.totalResults} headlines.")
//        response.articles.foreach(a =>
//          println(s"${a.publishedAt} - ${a.source.name} - ${a.title} - ${a.content}"),
//        )
//      case None =>
//        throw new RuntimeException(s"Please provide a valid api key as $NewsApiKeyEnv")
//    }

  }
//    def create(createOrder: CreateOrder): F[OrderResponse] =
//      for {
//        id <- UUIDGen[F].randomUUID
//        now <- Clock[F].realTimeInstant
//        order = Order.fromCreateOrder(id, now, createOrder)
//        _ <- orderRepository.create(order)
//      } yield order.toResponse
//
//    override def list: F[List[OrderResponse]] =
//      orderRepository.list
//        .map(_.map(_.toResponse))
//
//    override def get(id: UUID): F[Option[OrderResponse]] =
//      orderRepository
//        .get(id)
//        .map(_.map(_.toResponse))
//
//    override def delete(id: UUID): F[Option[OrderResponse]] =
//      orderRepository
//        .delete(id)
//        .map(_.map(_.toResponse))
//  }
//
//  def make[F[_]: UUIDGen: FlatMap: Clock](orderRepository: OrderRepository[F]): OrderService[F] =
//    new Impl[F](orderRepository)

  def make[F[_]: UUIDGen: FlatMap](
      newsRepository: NewsRepository[F],
      newsClient: RetryingNewsClient[F],
  ): NewsService[F] =
    new Impl[F](newsRepository, newsClient)
}
