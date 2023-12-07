package ru.tinkoff.newsaggregator.domain.news

import enumeratum.{Enum, EnumEntry}
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.{Codec, Schema}
import tethys.enumeratum.TethysEnum

sealed trait NewsCountry extends EnumEntry
object NewsCountry extends Enum[NewsCountry] with TethysEnum[NewsCountry] {
  final case object ae extends NewsCountry
  final case object bg extends NewsCountry
  final case object cz extends NewsCountry
  final case object de extends NewsCountry
  final case object eg extends NewsCountry
  final case object no extends NewsCountry
  final case object ru extends NewsCountry
  final case object se extends NewsCountry
  final case object us extends NewsCountry
  final case object sa extends NewsCountry

  override def values: IndexedSeq[NewsCountry] = findValues

  implicit val featureSchema: Schema[NewsCountry] =
    Schema.derivedEnumeration[NewsCountry].defaultStringBased

  implicit val featureCodec: PlainCodec[NewsCountry] =
    Codec.derivedEnumeration[String, NewsCountry].defaultStringBased
}
