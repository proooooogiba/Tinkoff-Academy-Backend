package ru.tinkoff.petstore.api.news

import cats.effect.kernel.Async
import cats.implicits.toFlatMapOps
import io.circe.generic.auto.exportDecoder
import ru.tinkoff.petstore.api.commons.SttpResponseUtils
import ru.tinkoff.petstore.api.news.model.configuration.NewsClientConfiguration
import ru.tinkoff.petstore.api.news.model.response.NewsResponse
import ru.tinkoff.petstore.domain.news.{NewsCategory, NewsCountry}
import sttp.client3.{SttpBackend, UriContext, basicRequest}

trait NewsClient[F[_]] {
  def getByKeyWord(keyWord: String): F[Option[NewsResponse]]
  def getHeadlinesByCategory(category: NewsCategory): F[Option[NewsResponse]]
  def getHeadlinesByCountry(countryCode: NewsCountry): F[Option[NewsResponse]]
}

class HttpNewsClient[F[_]: Async](
    sttpBackend: SttpBackend[F, Any],
    newsClientConfiguration: NewsClientConfiguration,
) extends NewsClient[F] {

  override def getByKeyWord(keyWord: String): F[Option[NewsResponse]] = {
    val getByKeyWordUrl =
      uri"${newsClientConfiguration.url}/everything?q=$keyWord&apiKey=${newsClientConfiguration.key}"

    basicRequest
      .get(getByKeyWordUrl)
      .response(SttpResponseUtils.unwrapResponse[F, Option[NewsResponse]])
      .readTimeout(newsClientConfiguration.timeout)
      .send(sttpBackend)
      .flatMap(_.body)
  }

  override def getHeadlinesByCategory(category: NewsCategory): F[Option[NewsResponse]] = {
    val getHeadlinesByCategoryUrl =
      uri"${newsClientConfiguration.url}/top-headlines?category=$category&apiKey=${newsClientConfiguration.key}"

    basicRequest
      .get(getHeadlinesByCategoryUrl)
      .response(SttpResponseUtils.unwrapResponse[F, Option[NewsResponse]])
      .readTimeout(newsClientConfiguration.timeout)
      .send(sttpBackend)
      .flatMap(_.body)
  }

  override def getHeadlinesByCountry(countryCode: NewsCountry): F[Option[NewsResponse]] = {
    val getHeadlinesByCountryUrl =
      uri"${newsClientConfiguration.url}/top-headlines?country=${countryCode.entryName}&apiKey=${newsClientConfiguration.key}"

    println(getHeadlinesByCountryUrl)
    basicRequest
      .get(getHeadlinesByCountryUrl)
      .response(SttpResponseUtils.unwrapResponse[F, Option[NewsResponse]])
      .readTimeout(newsClientConfiguration.timeout)
      .send(sttpBackend)
      .flatMap(_.body)
  }
}
