package ru.tinkoff.petstore.api.service

import cats.MonadThrow
import retry.{Sleep, retryingOnSomeErrors}
import ru.tinkoff.petstore.api.commons.RetryUtils
import ru.tinkoff.petstore.api.news.NewsClient
import ru.tinkoff.petstore.api.news.model.response.NewsResponse

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
}
