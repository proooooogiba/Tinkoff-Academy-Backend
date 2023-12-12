package ru.tinkoff.newsaggregator.utils

import io.circe.Json
import io.circe.parser.parse

trait TestUtils {
  def parseAsJsonUnsafe(str: String): Json = parse(str).fold(
    _ => throw new RuntimeException("Json's parsing is failed"),
    identity,
  )
}
