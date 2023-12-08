package ru.tinkoff.newsaggregator.wirings

import cats.effect.IO
import com.typesafe.config.{Config, ConfigFactory}
import ru.tinkoff.newsaggregator.config.newsclient.NewsClientConfiguration
import ru.tinkoff.newsaggregator.config.retry.RetryConfiguration
import ru.tinkoff.newsaggregator.utils.TestUtils
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client3.{Response, SttpBackend}
import sttp.model.StatusCode

trait DefaultWirings extends TestUtils {
  val config: Config = ConfigFactory.load()
  val orderClientConfiguration: NewsClientConfiguration = NewsClientConfiguration.load(config)
  val retryConfiguration: RetryConfiguration = RetryConfiguration.load(config)
  val sttpBackend: SttpBackend[IO, Any] = AsyncHttpClientCatsBackend
    .stub[IO]
    .whenRequestMatchesPartial {
//      case r
//          if r.uri.toString().contains("api/v1/db/save") && r.method.toString() == Methods.POST =>
//        Response.ok(
//          IO.pure(
//            parseAsJsonUnsafe(newsResponseString)
//              .as[NewsResponse]
//              .fold(_.toString(), response => response),
//          ),
//        )
//      case r if r.uri.toString().contains(s"api/v1/order") && r.method.toString() == Methods.GET =>
//        Response.ok(
//          IO.pure(
//            parseAsJsonUnsafe(orderResponseString)
//              .as[Option[OrderResponse]]
//              .fold(_.toString(), response => response),
//          ),
//        )
      case _ => Response("Not found", StatusCode.BadGateway)
    }
}
