package ru.tinkoff.petstore.api.news

import cats.effect.kernel.Async
import cats.implicits.toFlatMapOps
import io.circe.generic.auto.exportDecoder
import ru.tinkoff.petstore.api.commons.SttpResponseUtils
import ru.tinkoff.petstore.api.news.model.configuration.NewsClientConfiguration
import ru.tinkoff.petstore.domain.news.response.NewsAPIResponse
import ru.tinkoff.petstore.domain.news.{NewsCategory, NewsCountry}
import sttp.client3.{SttpBackend, UriContext, basicRequest}

trait NewsClient[F[_]] {
  def getByKeyWord(keyWord: String): F[Option[NewsAPIResponse]]
  def getHeadlinesByCategory(category: NewsCategory): F[Option[NewsAPIResponse]]
  def getHeadlinesByCountry(countryCode: NewsCountry): F[Option[NewsAPIResponse]]
}

class HttpNewsClient[F[_]: Async](
    sttpBackend: SttpBackend[F, Any],
    newsClientConfiguration: NewsClientConfiguration,
) extends NewsClient[F] {

  override def getByKeyWord(keyWord: String): F[Option[NewsAPIResponse]] = {
    val getByKeyWordUrl =
      uri"${newsClientConfiguration.url}/everything?q=$keyWord&apiKey=${newsClientConfiguration.key}"

    basicRequest
      .get(getByKeyWordUrl)
      .response(SttpResponseUtils.unwrapResponse[F, Option[NewsAPIResponse]])
      .readTimeout(newsClientConfiguration.timeout)
      .send(sttpBackend)
      .flatMap(_.body)
  }

  override def getHeadlinesByCategory(category: NewsCategory): F[Option[NewsAPIResponse]] = {
    val getHeadlinesByCategoryUrl =
      uri"${newsClientConfiguration.url}/top-headlines?category=$category&apiKey=${newsClientConfiguration.key}"

    basicRequest
      .get(getHeadlinesByCategoryUrl)
      .response(SttpResponseUtils.unwrapResponse[F, Option[NewsAPIResponse]])
      .readTimeout(newsClientConfiguration.timeout)
      .send(sttpBackend)
      .flatMap(_.body)
  }

  override def getHeadlinesByCountry(countryCode: NewsCountry): F[Option[NewsAPIResponse]] = {
    val getHeadlinesByCountryUrl =
      uri"${newsClientConfiguration.url}/top-headlines?country=${countryCode.entryName}&apiKey=${newsClientConfiguration.key}"

    basicRequest
      .get(getHeadlinesByCountryUrl)
      .response(SttpResponseUtils.unwrapResponse[F, Option[NewsAPIResponse]])
      .readTimeout(newsClientConfiguration.timeout)
      .send(sttpBackend)
      .flatMap(_.body)
  }
}
