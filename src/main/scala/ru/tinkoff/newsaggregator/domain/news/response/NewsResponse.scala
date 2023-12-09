package ru.tinkoff.newsaggregator.domain.news.response

import ru.tinkoff.newsaggregator.common.tethys.TethysInstances
import ru.tinkoff.newsaggregator.domain.news.News
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
) {
  def toNews: News =
    News(
      id,
      source_id,
      source_name,
      author,
      title,
      description,
      url,
      urlToImage,
      publishedAt,
      content,
    )
}

object NewsResponse extends TethysInstances {
  implicit val newsResponseReader: JsonReader[NewsResponse] = jsonReader

  implicit val newsResponseWriter: JsonWriter[NewsResponse] = jsonWriter

  implicit val newsResponseSchema: Schema[NewsResponse] = Schema.derived
    .description("Новость")



}
