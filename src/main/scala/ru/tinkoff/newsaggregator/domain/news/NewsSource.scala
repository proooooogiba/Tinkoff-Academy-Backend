package ru.tinkoff.newsaggregator.domain.news

import ru.tinkoff.newsaggregator.common.tethys.TethysInstances
import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

case class NewsSource(id: Option[String], name: String)

object NewsSource extends TethysInstances {
  implicit val newsSourceReader: JsonReader[NewsSource] = jsonReader

  implicit val newsSourceWriter: JsonWriter[NewsSource] = jsonWriter

  implicit val newsSourceSchema: Schema[NewsSource] = Schema.derived
    .description("Запрос создания заказа в магазине")
}
