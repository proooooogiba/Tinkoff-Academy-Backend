package ru.tinkoff.newsaggregator.domain.news.request

import ru.tinkoff.newsaggregator.common.tethys.TethysInstances
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import java.time.ZonedDateTime

case class CreateNewsRequest(
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

object CreateNewsRequest extends TethysInstances {
  implicit val createNewsRequestReader: JsonReader[CreateNewsRequest] = jsonReader

  implicit val createNewsRequestWriter: JsonWriter[CreateNewsRequest] = jsonWriter

  implicit val createNewsRequestSchema: Schema[CreateNewsRequest] = Schema.derived
    .description("Новость")
}
