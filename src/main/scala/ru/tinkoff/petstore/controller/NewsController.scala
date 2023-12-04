package ru.tinkoff.petstore.controller

import ru.tinkoff.petstore.common.controller.Controller
import ru.tinkoff.petstore.domain.news.NewsResponse
import ru.tinkoff.petstore.service.NewsService
import sttp.tapir._
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.ServerEndpoint

class NewsController[F[_]](newsService: NewsService[F]) extends Controller[F] {

  val getNewsByKeyWord: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить статьи по ключевому слову")
      .in("api" / "v1" / "news" / path[String]("keyWord"))
      .out(jsonBody[Option[NewsResponse]])
      .serverLogicSuccess(newsService.searchByKeyWord)

//  val deleteOrder: ServerEndpoint[Any, F] =
//    endpoint.delete
//      .summary("Удалить заказов")
//      .in("api" / "v1" / "order" / path[UUID]("orderId"))
//      .out(jsonBody[Option[OrderResponse]])
//      .serverLogicSuccess(orderService.delete)
//
//  override val endpoints: List[ServerEndpoint[Any, F]] =
//    List(createOrder, listOrders, getOrder, deleteOrder)
//      .map(_.withTag("Order"))

  override def endpoints: List[ServerEndpoint[Any, F]] =
    List(getNewsByKeyWord)
      .map(_.withTag("News"))
}

object NewsController {
  def make[F[_]](newsService: NewsService[F]): NewsController[F] =
    new NewsController[F](newsService)
}
