package ru.tinkoff.petstore.domain.news

import ru.tinkoff.petstore.common.tethys.TethysInstances
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

case class Article(
    source: NewsSource,
    author: Option[String],
    title: String,
    description: Option[String],
    url: String,
    urlToImage: Option[String],
    publishedAt: String,
    content: Option[String],
)

object Article extends TethysInstances {
  implicit val articleReader: JsonReader[Article] = jsonReader

  implicit val articleWriter: JsonWriter[Article] = jsonWriter

  implicit val articleSchema: Schema[Article] = Schema.derived
    .description("Список статей по ключевому слову")
}
