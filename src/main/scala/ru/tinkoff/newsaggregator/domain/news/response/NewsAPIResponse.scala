package ru.tinkoff.newsaggregator.domain.news.response

import ru.tinkoff.newsaggregator.common.tethys.TethysInstances
import sttp.tapir.Schema
import tethys.derivation.semiauto._
import tethys.{JsonReader, JsonWriter}
final case class NewsAPIResponse(
    status: String,
    totalResults: Int,
    articles: List[ArticleAPIResponse],
)
object NewsAPIResponse extends TethysInstances {
  implicit val newsResponseReader: JsonReader[NewsAPIResponse] = jsonReader

  implicit val newsResponseWriter: JsonWriter[NewsAPIResponse] = jsonWriter

  implicit val newsResponseSchema: Schema[NewsAPIResponse] = Schema.derived
    .description("Список статей по ключевому слову")
}
