package ru.tinkoff.newsaggregator.domain.news

import enumeratum.{Enum, EnumEntry}
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.{Codec, Schema}
import tethys.enumeratum.TethysEnum

sealed trait NewsCategory extends EnumEntry

object NewsCategory extends Enum[NewsCategory] with TethysEnum[NewsCategory] {
  final case object Business extends NewsCategory
  final case object Entertainment extends NewsCategory
  final case object General extends NewsCategory
  final case object Health extends NewsCategory
  final case object Science extends NewsCategory
  final case object Sports extends NewsCategory
  final case object Technology extends NewsCategory

  override def values: IndexedSeq[NewsCategory] = findValues

  implicit val featureSchema: Schema[NewsCategory] =
    Schema.derivedEnumeration[NewsCategory].defaultStringBased

  implicit val featureCodec: PlainCodec[NewsCategory] =
    Codec.derivedEnumeration[String, NewsCategory].defaultStringBased
}
