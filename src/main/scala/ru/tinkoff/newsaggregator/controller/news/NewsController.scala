package ru.tinkoff.newsaggregator.controller.news

import cats.Monad
import cats.Monad.ops.toAllMonadOps
import ru.tinkoff.newsaggregator.common.controller.Controller
import ru.tinkoff.newsaggregator.controller.news.ControllerErrors._
import ru.tinkoff.newsaggregator.controller.news.examples.CreateNewsRequestExample.{
  creationRequestExample,
  newsResponseExample,
}
import ru.tinkoff.newsaggregator.controller.news.examples.NewsAPIResponseExample.okAPIExample
import ru.tinkoff.newsaggregator.controller.news.examples.NewsDBResponseExample.okDBExample
import ru.tinkoff.newsaggregator.domain.news.request.CreateNewsRequest
import ru.tinkoff.newsaggregator.domain.news.response.NewsAPIResponse
import ru.tinkoff.newsaggregator.domain.news.{NewsCategory, NewsCountry, NewsResponse}
import ru.tinkoff.newsaggregator.service.NewsService
import sttp.model.StatusCode.{NotFound, Ok}
import sttp.tapir._
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.ServerEndpoint
import tethys.JsonObjectWriter.lowPriorityWriter
import tethys.derivation.auto.{jsonReaderMaterializer, jsonWriterMaterializer}

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
        statusCode(Ok).and(
          jsonBody[NewsAPIResponse]
            .description("Ответ от API с массивом новостей")
            .example(okAPIExample),
        ),
      )
      .errorOut(
        oneOf[Either[ServerError, UserError]](
          notFoundUserError,
          internalServerError,
        ),
      )
      .serverLogic { keyWord =>
        newsService
          .getByKeyWord(keyWord)
          .map {
            case Some(news) =>
              if (news.status == "error") {
                errorOfExternalService
              } else if (news.totalResults == 0)
                Left(
                  Right(ResourceNotFound(s"Новости по ключевому слову $keyWord не были найдены")),
                )
              else Right(news)
            case None => errorToConnectExternalService
          }
      }

  val getHeadlinesByCategory: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить самые популярные новости по категории")
      .in("api" / "v1" / "news-api" / "category" / path[NewsCategory]("category"))
      .out(
        statusCode(Ok).and(
          jsonBody[NewsAPIResponse]
            .description("Ответ от API с самыми популярными новостями по категориям")
            .example(okAPIExample),
        ),
      )
      .errorOut(
        oneOf[Either[ServerError, UserError]](
          internalServerError,
        ),
      )
      .serverLogic { category =>
        newsService.getHeadlinesByCategory(category).map {
          case Some(news) =>
            if (news.status == "error") {
              errorOfExternalService
            } else Right(news)
          case None => errorToConnectExternalService
        }
      }

  val getHeadlinesByCountry: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить самый популярные новости по странам")
      .in("api" / "v1" / "news-api" / "country" / path[NewsCountry]("country"))
      .out(
        statusCode(Ok).and(
          jsonBody[NewsAPIResponse]
            .description("Ответ от API с самыми популярными новостями по странам")
            .example(okAPIExample),
        ),
      )
      .errorOut(
        oneOf[Either[ServerError, UserError]](
          internalServerError,
        ),
      )
      .serverLogic { country =>
        newsService.getHeadlinesByCountry(country).map {
          case Some(news) =>
            if (news.status == "error") {
              errorOfExternalService
            } else Right(news)
          case None => errorToConnectExternalService
        }
      }

  val createNews: ServerEndpoint[Any, F] =
    endpoint.post
      .summary("Создать новость")
      .in("api" / "v1" / "db" / "save")
      .in(
        jsonBody[CreateNewsRequest]
          .description("Сохранение новости в базу данных")
          .example(creationRequestExample),
      )
      .out(
        statusCode(Ok).and(
          jsonBody[NewsResponse]
            .description("Возврат сохранённой новости с присвоенным uuid")
            .example(newsResponseExample),
        ),
      )
      .serverLogicSuccess(newsService.create)

  val listNews: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Список добавленных новостей")
      .in("api" / "v1" / "db" / "all")
      .out(
        statusCode(Ok).and(
          jsonBody[List[NewsResponse]]
            .description("Сохранённые новости успешно найдены")
            .example(okDBExample),
        ),
      )
      .errorOut(
        statusCode(NotFound).and(
          jsonBody[ResourceNotFound]
            .description("Сохранённые новости не найдены")
            .example(resourceNotFoundError),
        ),
      )
      .serverLogic { _ =>
        newsService.list.map { list =>
          if (list.isEmpty) {
            Left(resourceNotFoundError)
          } else {
            Right(list)
          }
        }
      }

  val getNews: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить новость по uuid")
      .in("api" / "v1" / "db" / "get" / path[UUID]("newsID"))
      .out(
        statusCode(Ok).and(
          jsonBody[NewsResponse]
            .description("Сохранённая новость найдена успешно")
            .example(newsResponseExample),
        ),
      )
      .errorOut(
        statusCode(NotFound).and(
          jsonBody[ResourceNotFound]
            .description("Новость не была найдена")
            .example(resourceNotFoundGet),
        ),
      )
      .serverLogic { newsID =>
        newsService
          .get(newsID)
          .map {
            case Some(news) => Right(news)
            case None       => Left(resourceNotFoundGet)
          }
      }

  val deleteNews: ServerEndpoint[Any, F] =
    endpoint.delete
      .summary("Удалить новость по uuid")
      .in("api" / "v1" / "db" / "delete" / path[UUID]("newsID"))
      .out(
        statusCode(Ok).and(
          jsonBody[NewsResponse]
            .description("Новость удалена")
            .example(newsResponseExample),
        ),
      )
      .errorOut(
        statusCode(NotFound).and(
          jsonBody[ResourceNotFound]
            .description("Новость не была найдена")
            .example(resourceNotFoundDel),
        ),
      )
      .serverLogic { newsID =>
        newsService
          .delete(newsID)
          .map {
            case Some(news) => Right(news)
            case None       => Left(resourceNotFoundDel)
          }
      }

  val getByKeyWordFromDB: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Найти по ключевым статьям")
      .in("api" / "v1" / "db" / "keyWord" / path[String]("keyWord"))
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
        "api" / "v1" / "db" / "dateRange" /
          path[LocalDate]("start")
            .description("Начало диапазона поиска")
            .example(LocalDate.parse("2023-12-11"))
          / path[LocalDate](name = "end")
            .description("Конец диапазона поиска")
            .example(LocalDate.parse("2023-12-13")),
      )
      .out(
        statusCode(Ok).and(
          jsonBody[List[NewsResponse]]
            .description("Новости в данном диапазоне были успешной найдены")
            .example(okDBExample),
        ),
      )
      .errorOut(
        statusCode(NotFound).and(
          jsonBody[ResourceNotFound]
            .description("Новости в данном диапазоне не были найдены")
            .example(resourceNotFoundError),
        ),
      )
      .serverLogic { tupleDate =>
        newsService
          .listByDate(
            tupleDate._1.atStartOfDay(ZoneId.systemDefault()),
            tupleDate._2.atStartOfDay(ZoneId.systemDefault()),
          )
          .map { list =>
            if (list.isEmpty) {
              Left(
                ResourceNotFound(
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
