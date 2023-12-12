package ru.tinkoff.newsaggregator.wirings

import cats.effect.IO
import io.circe.generic.auto.exportDecoder
import org.asynchttpclient.util.HttpConstants.Methods
import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader
import ru.tinkoff.newsaggregator.config.AppConfig
import ru.tinkoff.newsaggregator.config.newsclient.NewsClientConfiguration
import ru.tinkoff.newsaggregator.config.retry.RetryConfiguration
import ru.tinkoff.newsaggregator.controller.news.examples.NewsAPIResponseExample.okAPIExample
import ru.tinkoff.newsaggregator.domain.news.response.NewsAPIResponse
import ru.tinkoff.newsaggregator.utils.TestUtils
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client3.{Response, SttpBackend}
import sttp.model.StatusCode
import tethys.JsonWriterOps
import tethys.jackson.jacksonTokenWriterProducer

trait DefaultWirings extends TestUtils {
  val config: AppConfig =
    ConfigSource.file("src/it/resources/reference.conf").loadOrThrow[AppConfig]
  val newsClientConfiguration: NewsClientConfiguration = config.api
  val retryConfiguration: RetryConfiguration = config.retry
  val sttpBackend: SttpBackend[IO, Any] = AsyncHttpClientCatsBackend
    .stub[IO]
    .whenRequestMatchesPartial {
      case r
          if r.uri.toString().contains(s"everything?q") && r.method
            .toString() == Methods.GET =>
        Response.ok(
          IO.pure(
            parseAsJsonUnsafe(okAPIExample.asJson)
              .as[Option[NewsAPIResponse]]
              .fold(_.toString(), response => response),
          ),
        )
      case r
          if r.uri.toString().contains(s"top-headlines?category") && r.method
            .toString() == Methods.GET =>
        Response.ok(
          IO.pure(
            parseAsJsonUnsafe(okAPIExample.asJson)
              .as[Option[NewsAPIResponse]]
              .fold(_.toString(), response => response),
          ),
        )
      case r
          if r.uri.toString().contains(s"top-headlines?country") && r.method
            .toString() == Methods.GET =>
        Response.ok(
          IO.pure(
            parseAsJsonUnsafe(okAPIExample.asJson)
              .as[Option[NewsAPIResponse]]
              .fold(_.toString(), response => response),
          ),
        )
      case _ => Response("Not found", StatusCode.BadGateway)
    }
}
