package ru.tinkoff.petstore.api.service

import cats.MonadThrow
import retry.{Sleep, retryingOnSomeErrors}
import ru.tinkoff.petstore.api.commons.RetryUtils
import ru.tinkoff.petstore.api.news.NewsClient
import ru.tinkoff.petstore.api.news.model.response.NewsResponse
import ru.tinkoff.petstore.domain.news.{NewsCategory, NewsCountry}

class RetryingNewsClient[F[_]: MonadThrow: Sleep](
    newsClient: NewsClient[F],
    retryUtils: RetryUtils[F],
) extends NewsClient[F] {
  override def getByKeyWord(keyWord: String): F[Option[NewsResponse]] =
    retryingOnSomeErrors[Option[NewsResponse]](
      isWorthRetrying = retryUtils.isTimeoutException,
      policy = retryUtils.policy,
      onError = retryUtils.onError,
    )(newsClient.getByKeyWord(keyWord))

  override def getHeadlinesByCategory(category: NewsCategory): F[Option[NewsResponse]] =
    retryingOnSomeErrors[Option[NewsResponse]](
      isWorthRetrying = retryUtils.isTimeoutException,
      policy = retryUtils.policy,
      onError = retryUtils.onError,
    )(newsClient.getHeadlinesByCategory(category))

  override def getHeadlinesByCountry(countryCode: NewsCountry): F[Option[NewsResponse]] =
    retryingOnSomeErrors[Option[NewsResponse]](
      isWorthRetrying = retryUtils.isTimeoutException,
      policy = retryUtils.policy,
      onError = retryUtils.onError,
    )(newsClient.getHeadlinesByCountry(countryCode))
}
