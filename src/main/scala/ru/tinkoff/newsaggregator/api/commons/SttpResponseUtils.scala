package ru.tinkoff.newsaggregator.api.commons

import cats.ApplicativeThrow
import io.circe.Decoder
import sttp.client3.ResponseAs
import sttp.client3.circe.asJsonAlways

object SttpResponseUtils {
  def unwrapResponse[F[_]: ApplicativeThrow, T: Decoder]: ResponseAs[F[T], Any] =
    asJsonAlways[T].map(ApplicativeThrow[F].fromEither(_))
}
