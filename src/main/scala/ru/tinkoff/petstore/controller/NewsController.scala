package ru.tinkoff.petstore.controller

import ru.tinkoff.petstore.common.controller.Controller
import ru.tinkoff.petstore.domain.news.request.CreateNewsRequest
import ru.tinkoff.petstore.domain.news.response.NewsAPIResponse
import ru.tinkoff.petstore.domain.news.{NewsCategory, NewsCountry, NewsResponse}
import ru.tinkoff.petstore.service.NewsService
import sttp.tapir._
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.ServerEndpoint
import tethys.JsonObjectWriter.lowPriorityWriter
import tethys.derivation.auto.jsonWriterMaterializer

class NewsController[F[_]](newsService: NewsService[F]) extends Controller[F] {

  val getNewsByKeyWord: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить статьи по ключевому слову")
      .in("api" / "v1" / "keyWord" / path[String]("keyWord"))
      .out(jsonBody[Option[NewsAPIResponse]])
      .serverLogicSuccess(newsService.getByKeyWord)

  val getHeadlinesByCategory: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить самый популярные новости по категории")
      .in("api" / "v1" / "category" / path[NewsCategory]("category"))
      .out(jsonBody[Option[NewsAPIResponse]])
      .serverLogicSuccess(newsService.getHeadlinesByCategory)

  val getHeadlinesByCountry: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить самый популярные новости по странам")
      .in("api" / "v1" / "country" / path[NewsCountry]("country"))
      .out(jsonBody[Option[NewsAPIResponse]])
      .serverLogicSuccess(newsService.getHeadlinesByCountry)

  val createNews: ServerEndpoint[Any, F] =
    endpoint.post
      .summary("Создать новость")
      .in("api" / "v1" / "create")
      .in(jsonBody[CreateNewsRequest])
      .out(jsonBody[NewsResponse])
      .serverLogicSuccess(newsService.create)

  override def endpoints: List[ServerEndpoint[Any, F]] =
    List(getNewsByKeyWord, getHeadlinesByCategory, getHeadlinesByCountry, createNews)
      .map(_.withTag("News"))
}

object NewsController {
  def make[F[_]](newsService: NewsService[F]): NewsController[F] =
    new NewsController[F](newsService)
}
