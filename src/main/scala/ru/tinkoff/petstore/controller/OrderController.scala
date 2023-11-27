package ru.tinkoff.petstore.controller

import ru.tinkoff.petstore.common.controller.Controller
import ru.tinkoff.petstore.domain.order.{CreateOrder, OrderResponse}
import ru.tinkoff.petstore.service.OrderService
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir._

import java.util.UUID

class OrderController[F[_]](orderService: OrderService[F]) extends Controller[F] {
  val createOrder: ServerEndpoint[Any, F] =
    endpoint.post
      .summary("Создать заказ")
      .in("api" / "v1" / "order")
      .in(jsonBody[CreateOrder])
      .out(jsonBody[OrderResponse])
      .serverLogicSuccess(orderService.create)

  val listOrders: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Список заказов")
      .in("api" / "v1" / "order")
      .out(jsonBody[List[OrderResponse]])
      .serverLogicSuccess(_ => orderService.list)

  val getOrder: ServerEndpoint[Any, F] =
    endpoint.get
      .summary("Получить заказ")
      .in("api" / "v1" / "order" / path[UUID]("orderId"))
      .out(jsonBody[Option[OrderResponse]])
      .serverLogicSuccess(orderService.get)

  val deleteOrder: ServerEndpoint[Any, F] =
    endpoint.delete
      .summary("Удалить заказов")
      .in("api" / "v1" / "order" / path[UUID]("orderId"))
      .out(jsonBody[Option[OrderResponse]])
      .serverLogicSuccess(orderService.delete)

  override val endpoints: List[ServerEndpoint[Any, F]] =
    List(createOrder, listOrders, getOrder, deleteOrder)
      .map(_.withTag("Order"))
}

object OrderController {
  def make[F[_]](orderService: OrderService[F]): OrderController[F] =
    new OrderController[F](orderService)
}
