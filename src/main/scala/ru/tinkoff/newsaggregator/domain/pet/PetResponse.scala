package ru.tinkoff.newsaggregator.domain.pet

import ru.tinkoff.newsaggregator.common.tethys.TethysInstances
import sttp.tapir.Schema
import tethys.derivation.semiauto._
import tethys.{JsonReader, JsonWriter}

import java.util.UUID

final case class PetResponse(
    id: UUID,
    name: String,
    category: PetCategory,
    description: String,
)

object PetResponse extends TethysInstances {
  implicit val petResponseReader: JsonReader[PetResponse] = jsonReader

  implicit val petResponseWriter: JsonWriter[PetResponse] = jsonWriter

  implicit val petResponseSchema: Schema[PetResponse] = Schema.derived
    .description("Питомец")
}
