package ru.tinkoff.petstore.domain.order

import ru.tinkoff.petstore.common.tethys.TethysInstances
import sttp.tapir.Schema
import tethys.derivation.semiauto._
import tethys.{JsonReader, JsonWriter}

import java.time.Instant
import java.util.UUID

final case class OrderResponse(
    id: UUID,
    petId: UUID,
    date: Instant,
)

object OrderResponse extends TethysInstances {
  implicit val orderResponseReader: JsonReader[OrderResponse] = jsonReader

  implicit val orderResponseWriter: JsonWriter[OrderResponse] = jsonWriter

  implicit val orderResponseSchema: Schema[OrderResponse] = Schema.derived
    .description("Заказ")
}
