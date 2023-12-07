package ru.tinkoff.newsaggregator.domain.pet

import sttp.tapir.Schema
import tethys.derivation.semiauto._
import tethys.{JsonReader, JsonWriter}

case class CreatePet(
    name: String,
    category: PetCategory,
    description: String,
)

object CreatePet {
  implicit val createPetReader: JsonReader[CreatePet] = jsonReader

  implicit val createPetWriter: JsonWriter[CreatePet] = jsonWriter

  implicit val createPetSchema: Schema[CreatePet] = Schema.derived
    .description("Запрос добавления животного в магазин")
}
