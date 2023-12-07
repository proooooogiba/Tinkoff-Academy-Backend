package ru.tinkoff.newsaggregator.domain.pet

import enumeratum.{Enum, EnumEntry}
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.{Codec, Schema}
import tethys.enumeratum._

sealed trait PetCategory extends EnumEntry

object PetCategory extends Enum[PetCategory] with TethysEnum[PetCategory] {
  final case object Snake extends PetCategory
  final case object Amphibian extends PetCategory
  final case object Mammal extends PetCategory
  final case object Reptile extends PetCategory
  final case object Bird extends PetCategory
  final case object Fish extends PetCategory
  final case object Worm extends PetCategory

  override def values: IndexedSeq[PetCategory] = findValues

  implicit val featureSchema: Schema[PetCategory] =
    Schema.derivedEnumeration[PetCategory].defaultStringBased

  implicit val featureCodec: PlainCodec[PetCategory] =
    Codec.derivedEnumeration[String, PetCategory].defaultStringBased
}
