package ru.tinkoff.petstore.api.news

import cats.effect.kernel.Async
import cats.implicits.toFlatMapOps
import io.circe.generic.auto.exportDecoder
import ru.tinkoff.petstore.api.commons.SttpResponseUtils
import ru.tinkoff.petstore.api.news.model.configuration.NewsClientConfiguration
import ru.tinkoff.petstore.api.news.model.response.NewsResponse
import sttp.client3.{SttpBackend, UriContext, basicRequest}
trait NewsClient[F[_]] {
  def getByKeyWord(keyWord: String): F[Option[NewsResponse]]
}

class HttpNewsClient[F[_]: Async](
    sttpBackend: SttpBackend[F, Any],
    newsClientConfiguration: NewsClientConfiguration,
) extends NewsClient[F] {

  override def getByKeyWord(keyWord: String): F[Option[NewsResponse]] = {
    val getByKeyWordUrl =
      uri"${newsClientConfiguration.url}/everything?q=$keyWord&apiKey=${newsClientConfiguration.key}"

    println(getByKeyWordUrl)
    basicRequest
      .get(getByKeyWordUrl)
      .response(SttpResponseUtils.unwrapResponse[F, Option[NewsResponse]])
      .readTimeout(newsClientConfiguration.timeout)
      .send(sttpBackend)
      .flatMap(_.body)
  }
}
