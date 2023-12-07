package ru.tinkoff.newsaggregator.controller

import cats.Monad
import cats.Monad.ops.toAllMonadOps
import ru.tinkoff.newsaggregator.common.controller.Controller
import ru.tinkoff.newsaggregator.domain.news.request.CreateNewsRequest
import ru.tinkoff.newsaggregator.domain.news.response.NewsAPIResponse
import ru.tinkoff.newsaggregator.domain.news.response.NewsAPIResponseExample.okAPIExample
import ru.tinkoff.newsaggregator.domain.news.{NewsCategory, NewsCountry, NewsResponse}
import ru.tinkoff.newsaggregator.service.NewsService
import sttp.model.StatusCode.{NotFound, Ok}
import sttp.tapir._
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.ServerEndpoint
import tethys.JsonObjectWriter.lowPriorityWriter
import tethys.derivation.auto.jsonWriterMaterializer

import java.time.{LocalDate, ZoneId}
import java.util.UUID

class NewsController[F[_]: Monad](newsService: NewsService[F]) extends Controller[F] {

  val getNewsByKeyWord: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить статьи по ключевому слову")
      .in(
        "api" / "v1" / "news-api" / "keyWord" /
          path[String]("keyWord")
            .description("Ключевое слово")
            .example("Scala"),
      )
      .out(
        jsonBody[Option[NewsAPIResponse]]
          .description("Ответ от API с массивом новостей")
          .example(
            Some(
              okAPIExample,
            ),
          ),
      )
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

  val listNews: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Список добавленных новостей")
      .in("api" / "v1" / "db" / "all")
      .out(
        statusCode(Ok).and(
          jsonBody[List[NewsResponse]]
            .description("Сохранённые новости не найдены")
            .example(
            ),
        ),
      )
      .errorOut(
        statusCode(NotFound).and(
          jsonBody[String]
            .description("Сохранённые новости не найдены")
            .example("Сохранённые новости не найдены: добавьте и они здесь появятся"),
        ),
      )
      .serverLogic { _ =>
        newsService.list.map { list =>
          if (list.isEmpty) {
            Left("Сохранённые новости не найдены: добавьте и они здесь появятся")
          } else {
            Right(list)
          }
        }
      }

  val getNews: ServerEndpoint[Any, F] =
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

  val deleteNews: ServerEndpoint[Any, F] =
    endpoint.delete
      .summary("Удалить новость по uuid")
      .in("api" / "v1" / "db" / "delete" / path[UUID]("newsID"))
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

  val getByKeyWordFromDB: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Найти по ключевым статьям")
      .in("api" / "v1" / "db" / "byKeyWord" / path[String]("keyWord"))
      .out(jsonBody[List[NewsResponse]])
      .errorOut(statusCode.and(jsonBody[String]))
      .serverLogic { keyWord =>
        newsService
          .getByKeyWordFromDB(keyWord)
          .map { list =>
            if (list.isEmpty) {
              Left((NotFound, s"Сохранённые новости по ключевому слову $keyWord не найдены"))
            } else {
              Right(list)
            }
          }
      }

  val getNewsByPublishedRange: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить сохранённые новости по времени публикации")
      .in(
        "api" / "v1" / "db" / "byDateRange" /
          path[LocalDate]("start")
            .description("Начало диапазона поиска")
            .example(LocalDate.parse("2023-12-11"))

          / path[LocalDate](name = "end")
            .description("Конец диапазона поиска")
            .example(LocalDate.parse("2023-12-13")),
      )
      .out(jsonBody[List[NewsResponse]])
      .errorOut(statusCode.and(jsonBody[String]))
      .serverLogic { tupleDate =>
        newsService
          .listByDate(
            tupleDate._1.atStartOfDay(ZoneId.systemDefault()),
            tupleDate._2.atStartOfDay(ZoneId.systemDefault()),
          )
          .map { list =>
            if (list.isEmpty) {
              Left(
                (
                  NotFound,
                  "Сохранённые новости в этом диапазоне не найдены",
                ),
              )
            } else {
              Right(list)
            }
          }
      }

  override def endpoints: List[ServerEndpoint[Any, F]] =
    List(
      getNewsByKeyWord,
      getHeadlinesByCategory,
      getHeadlinesByCountry,
      createNews,
      listNews,
      getNews,
      deleteNews,
      getByKeyWordFromDB,
      getNewsByPublishedRange,
    )
      .map(_.withTag("News"))
}

object NewsController {
  def make[F[_]: Monad](newsService: NewsService[F]): NewsController[F] =
    new NewsController[F](newsService)
}
