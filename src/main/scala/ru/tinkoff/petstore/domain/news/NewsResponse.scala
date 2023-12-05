package ru.tinkoff.petstore.domain.news

import ru.tinkoff.petstore.common.tethys.TethysInstances
import sttp.tapir.Schema
import tethys.derivation.semiauto._
import tethys.{JsonReader, JsonWriter}

import java.time.ZonedDateTime
import java.util.UUID

case class NewsResponse(
    id: UUID,
    source_id: Option[String],
    source_name: String,
    author: Option[String],
    title: String,
    description: Option[String],
    url: String,
    urlToImage: Option[String],
    publishedAt: ZonedDateTime,
    content: Option[String],
)

object NewsResponse extends TethysInstances {
  implicit val newsResponseReader: JsonReader[NewsResponse] = jsonReader

  implicit val newsResponseWriter: JsonWriter[NewsResponse] = jsonWriter

  implicit val newsResponseSchema: Schema[NewsResponse] = Schema.derived
    .description("Новость")
}
