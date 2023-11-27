package ru.tinkoff.petstore.repository.postgresql

import io.getquill.MappedEncoding
import ru.tinkoff.petstore.domain.pet.PetCategory

import java.time.{Instant, ZoneOffset, ZonedDateTime}

trait QuillInstances {
  implicit val categoryEncoder: MappedEncoding[String, PetCategory] = MappedEncoding(
    PetCategory.withName,
  )
  implicit val categoryDecoder: MappedEncoding[PetCategory, String] = MappedEncoding(_.entryName)

  implicit val instantEncoder: MappedEncoding[ZonedDateTime, Instant] = MappedEncoding(_.toInstant)
  implicit val instantDecoder: MappedEncoding[Instant, ZonedDateTime] = MappedEncoding(
    _.atZone(ZoneOffset.UTC),
  )
}
