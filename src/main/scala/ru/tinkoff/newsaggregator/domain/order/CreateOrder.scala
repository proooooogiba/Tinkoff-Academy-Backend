package ru.tinkoff.newsaggregator.domain.order

import ru.tinkoff.newsaggregator.common.tethys.TethysInstances
import sttp.tapir.Schema
import tethys.derivation.semiauto._
import tethys.{JsonReader, JsonWriter}

import java.util.UUID

case class CreateOrder(petId: UUID)

object CreateOrder extends TethysInstances {
  implicit val createOrderReader: JsonReader[CreateOrder] = jsonReader

  implicit val createOrderWriter: JsonWriter[CreateOrder] = jsonWriter

  implicit val createOrderSchema: Schema[CreateOrder] = Schema.derived
    .description("Запрос создания заказа в магазине")
}
