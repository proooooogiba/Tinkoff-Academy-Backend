package ru.tinkoff.newsaggregator.repository.postgresql

import io.getquill.MappedEncoding

import java.time.{Instant, ZoneOffset, ZonedDateTime}

trait QuillInstances {
  implicit val instantEncoder: MappedEncoding[ZonedDateTime, Instant] = MappedEncoding(_.toInstant)
  implicit val instantDecoder: MappedEncoding[Instant, ZonedDateTime] = MappedEncoding(
    _.atZone(ZoneOffset.UTC),
  )
}
