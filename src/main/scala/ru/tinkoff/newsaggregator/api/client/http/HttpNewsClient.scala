package ru.tinkoff.newsaggregator.api.client.http

import cats.effect.kernel.Async
import cats.implicits.toFlatMapOps
import io.circe.generic.auto.exportDecoder
import ru.tinkoff.newsaggregator.api.client.NewsClient
import ru.tinkoff.newsaggregator.api.commons.SttpResponseUtils
import ru.tinkoff.newsaggregator.config.newsclient.NewsClientConfiguration
import ru.tinkoff.newsaggregator.domain.news.response.NewsAPIResponse
import ru.tinkoff.newsaggregator.domain.news.{NewsCategory, NewsCountry}
import sttp.client3.{SttpBackend, UriContext, basicRequest}
import sttp.model.Uri

class HttpNewsClient[F[_]: Async](
    sttpBackend: SttpBackend[F, Any],
    newsClientConfiguration: NewsClientConfiguration,
) extends NewsClient[F] {

  private def sendRequest(request: Uri): F[Option[NewsAPIResponse]] =
    basicRequest
      .get(request)
      .response(SttpResponseUtils.unwrapResponse[F, Option[NewsAPIResponse]])
      .readTimeout(newsClientConfiguration.timeout)
      .send(sttpBackend)
      .flatMap(_.body)

  override def getByKeyWord(keyWord: String): F[Option[NewsAPIResponse]] = {
    val getByKeyWordUrl =
      uri"${newsClientConfiguration.url}/everything?q=$keyWord&apiKey=${newsClientConfiguration.key}"

    sendRequest(getByKeyWordUrl)
  }

  override def getHeadlinesByCategory(category: NewsCategory): F[Option[NewsAPIResponse]] = {
    val getHeadlinesByCategoryUrl =
      uri"${newsClientConfiguration.url}/top-headlines?category=$category&apiKey=${newsClientConfiguration.key}"

    sendRequest(getHeadlinesByCategoryUrl)
  }

  override def getHeadlinesByCountry(countryCode: NewsCountry): F[Option[NewsAPIResponse]] = {
    val getHeadlinesByCountryUrl =
      uri"${newsClientConfiguration.url}/top-headlines?country=${countryCode.entryName}&apiKey=${newsClientConfiguration.key}"

    sendRequest(getHeadlinesByCountryUrl)
  }
}
