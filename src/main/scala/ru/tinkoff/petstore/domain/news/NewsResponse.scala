//package ru.tinkoff.petstore.domain.news
//
//import ru.tinkoff.petstore.common.tethys.TethysInstances
//import sttp.tapir.Schema
//import tethys.derivation.semiauto._
//import tethys.{JsonReader, JsonWriter}
//import io.circe.Decoder
//import io.circe.generic.semiauto.deriveDecoder
//
//final case class NewsResponse(
//    status: String,
//    totalResults: Int,
//    articles: List[Article],
//)
//object NewsResponse extends TethysInstances {
//  implicit val newsResponseDecoder: Decoder[NewsResponse] = deriveDecoder[NewsResponse]
//
//  implicit val newsResponseReader: JsonReader[NewsResponse] = jsonReader
//
//  implicit val newsResponseWriter: JsonWriter[NewsResponse] = jsonWriter
//
//  implicit val newsResponseSchema: Schema[NewsResponse] = Schema.derived
//    .description("Список статей по ключевому слову")
//}
