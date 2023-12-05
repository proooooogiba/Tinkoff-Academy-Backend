package ru.tinkoff.petstore.controller

import cats.Monad
import cats.Monad.ops.toAllMonadOps
import ru.tinkoff.petstore.common.controller.Controller
import ru.tinkoff.petstore.domain.news.request.CreateNewsRequest
import ru.tinkoff.petstore.domain.news.response.NewsAPIResponse
import ru.tinkoff.petstore.domain.news.{NewsCategory, NewsCountry, NewsResponse}
import ru.tinkoff.petstore.service.NewsService
import sttp.model.StatusCode.NotFound
import sttp.tapir._
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.ServerEndpoint
import tethys.JsonObjectWriter.lowPriorityWriter
import tethys.derivation.auto.jsonWriterMaterializer

import java.util.UUID

class NewsController[F[_]: Monad](newsService: NewsService[F]) extends Controller[F] {

  val getNewsByKeyWord: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить статьи по ключевому слову")
      .in("api" / "v1" / "news-api" / "keyWord" / path[String]("keyWord"))
      .out(jsonBody[Option[NewsAPIResponse]])
      .serverLogicSuccess(newsService.getByKeyWord)

  val getHeadlinesByCategory: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить самые популярные новости по категории")
      .in("api" / "v1" / "news-api" / "category" / path[NewsCategory]("category"))
      .out(jsonBody[Option[NewsAPIResponse]])
      .serverLogicSuccess(newsService.getHeadlinesByCategory)

  val getHeadlinesByCountry: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить самый популярные новости по странам")
      .in("api" / "v1" / "news-api" / "country" / path[NewsCountry]("country"))
      .out(jsonBody[Option[NewsAPIResponse]])
      .serverLogicSuccess(newsService.getHeadlinesByCountry)

  val createNews: ServerEndpoint[Any, F] =
    endpoint.post
      .summary("Создать новость")
      .in("api" / "v1" / "db" / "create")
      .in(jsonBody[CreateNewsRequest])
      .out(jsonBody[NewsResponse])
      .serverLogicSuccess(newsService.create)

  val listOrders: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Список добавленных новостей")
      .in("api" / "v1" / "db" / "showAll")
      .out(jsonBody[List[NewsResponse]])
      .errorOut(statusCode.and(jsonBody[String]))
      .serverLogic { _ =>
        newsService.list.map { list =>
          if (list.isEmpty) {
            Left((NotFound, "Сохранённые новости не найдены: добавьте и они здесь появятся"))
          } else {
            Right(list)
          }
        }
      }

  val getOrder: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить новость по uuid")
      .in("api" / "v1" / "db" / "get" / path[UUID]("newsID"))
      .out(jsonBody[Option[NewsResponse]])
      .errorOut(statusCode.and(jsonBody[String]))
      .serverLogic { newsID =>
        newsService
          .get(newsID)
          .map {
            case Some(news) => Right(Some(news))
            case None => Left((NotFound, "Новость не найдена: новости с данным uuid не существует"))
          }
      }

  val deleteOrder: ServerEndpoint[Any, F] =
    endpoint.delete
      .summary("Удалить новость по uuid")
      .in("api" / "v1" / "db" / "delete" / path[UUID]("orderId"))
      .out(jsonBody[Option[NewsResponse]])
      .errorOut(statusCode.and(jsonBody[String]))
      .serverLogic { newsID =>
        newsService
          .delete(newsID)
          .map {
            case Some(news) => Right(Some(news))
            case None => Left((NotFound, "Новость не удалена: новости с данным uuid не существует"))
          }
      }

  override def endpoints: List[ServerEndpoint[Any, F]] =
    List(
      getNewsByKeyWord,
      getHeadlinesByCategory,
      getHeadlinesByCountry,
      createNews,
      listOrders,
      getOrder,
      deleteOrder,
    )
      .map(_.withTag("News"))
}

object NewsController {
  def make[F[_]: Monad](newsService: NewsService[F]): NewsController[F] =
    new NewsController[F](newsService)
}
