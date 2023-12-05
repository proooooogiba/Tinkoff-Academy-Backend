package ru.tinkoff.petstore.domain.news.response

import ru.tinkoff.petstore.common.tethys.TethysInstances
import ru.tinkoff.petstore.domain.news.NewsSource
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import java.time.ZonedDateTime

case class ArticleAPIResponse(
    source: NewsSource,
    author: Option[String],
    title: String,
    description: Option[String],
    url: String,
    urlToImage: Option[String],
    publishedAt: ZonedDateTime,
    content: Option[String],
)

object ArticleAPIResponse extends TethysInstances {
  implicit val articleReader: JsonReader[ArticleAPIResponse] = jsonReader

  implicit val articleWriter: JsonWriter[ArticleAPIResponse] = jsonWriter

  implicit val articleSchema: Schema[ArticleAPIResponse] = Schema.derived
    .description("Список статей по ключевому слову")

}
