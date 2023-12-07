package ru.tinkoff.newsaggregator.api.client

import ru.tinkoff.newsaggregator.domain.news.{NewsCategory, NewsCountry}
import ru.tinkoff.newsaggregator.domain.news.response.NewsAPIResponse

trait NewsClient[F[_]] {
  def getByKeyWord(keyWord: String): F[Option[NewsAPIResponse]]
  def getHeadlinesByCategory(category: NewsCategory): F[Option[NewsAPIResponse]]
  def getHeadlinesByCountry(countryCode: NewsCountry): F[Option[NewsAPIResponse]]
}
