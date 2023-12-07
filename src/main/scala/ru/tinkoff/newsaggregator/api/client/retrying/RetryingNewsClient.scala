package ru.tinkoff.newsaggregator.api.client.retrying

import cats.MonadThrow
import retry.{Sleep, retryingOnSomeErrors}
import ru.tinkoff.newsaggregator.api.client.NewsClient
import ru.tinkoff.newsaggregator.api.commons.RetryUtils
import ru.tinkoff.newsaggregator.domain.news.response.NewsAPIResponse
import ru.tinkoff.newsaggregator.domain.news.{NewsCategory, NewsCountry}

class RetryingNewsClient[F[_]: MonadThrow: Sleep](
    newsClient: NewsClient[F],
    retryUtils: RetryUtils[F],
) extends NewsClient[F] {
  override def getByKeyWord(keyWord: String): F[Option[NewsAPIResponse]] =
    retryingOnSomeErrors[Option[NewsAPIResponse]](
      isWorthRetrying = retryUtils.isTimeoutException,
      policy = retryUtils.policy,
      onError = retryUtils.onError,
    )(newsClient.getByKeyWord(keyWord))

  override def getHeadlinesByCategory(category: NewsCategory): F[Option[NewsAPIResponse]] =
    retryingOnSomeErrors[Option[NewsAPIResponse]](
      isWorthRetrying = retryUtils.isTimeoutException,
      policy = retryUtils.policy,
      onError = retryUtils.onError,
    )(newsClient.getHeadlinesByCategory(category))

  override def getHeadlinesByCountry(countryCode: NewsCountry): F[Option[NewsAPIResponse]] =
    retryingOnSomeErrors[Option[NewsAPIResponse]](
      isWorthRetrying = retryUtils.isTimeoutException,
      policy = retryUtils.policy,
      onError = retryUtils.onError,
    )(newsClient.getHeadlinesByCountry(countryCode))
}