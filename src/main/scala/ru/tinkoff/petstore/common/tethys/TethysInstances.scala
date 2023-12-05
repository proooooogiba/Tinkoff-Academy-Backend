package ru.tinkoff.petstore.common.tethys

import tethys.{JsonReader, JsonWriter}

import java.time.{Instant, ZonedDateTime}
import java.util.UUID

trait TethysInstances {
  implicit val instantReader: JsonReader[Instant] = JsonReader[String].map(Instant.parse)
  implicit val instantWriter: JsonWriter[Instant] = JsonWriter[String].contramap(_.toString)

  implicit val zonedDateTimeReader: JsonReader[ZonedDateTime] =
    JsonReader[String].map(ZonedDateTime.parse(_))
  implicit val zonedDateTimeWriter: JsonWriter[ZonedDateTime] =
    JsonWriter[String].contramap(_.toString)

  implicit val uuidReader: JsonReader[UUID] = JsonReader[String].map(UUID.fromString)
  implicit val uuidWriter: JsonWriter[UUID] = JsonWriter[String].contramap(_.toString)
}
