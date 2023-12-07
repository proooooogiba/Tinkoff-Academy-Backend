package ru.tinkoff.newsaggregator.api.commons

import cats.effect.kernel.Async
import cats.implicits.catsSyntaxApplicativeId
import org.typelevel.log4cats.Logger
import retry.RetryDetails.{GivingUp, WillDelayAndRetry}
import retry.{RetryDetails, RetryPolicies, RetryPolicy}
import ru.tinkoff.newsaggregator.config.retry.RetryConfiguration
import sttp.client3.SttpClientException.TimeoutException

trait RetryUtils[F[_]] {
  def onError(error: Throwable, retryDetails: RetryDetails): F[Unit]
  def policy: RetryPolicy[F]
  def isTimeoutException(e: Throwable): F[Boolean]
}

class RetryUtilsImpl[F[_]: Async](logger: Logger[F], retryConfiguration: RetryConfiguration)
    extends RetryUtils[F] {
  import retryConfiguration.{amount, duration}
  def policy: RetryPolicy[F] = RetryPolicies
    .limitRetriesByDelay[F](duration, RetryPolicies.limitRetries(amount))

  def isTimeoutException(e: Throwable): F[Boolean] = e match {
    case _: TimeoutException => true.pure[F]
    case _                   => false.pure[F]
  }

  def onError(error: Throwable, retryDetails: RetryDetails): F[Unit] = retryDetails match {
    case WillDelayAndRetry(_, retriesSoFar, _) =>
      logger.info(s"Failed to download with $error. So far we have retried $retriesSoFar times.")
    case GivingUp(totalRetries, _) =>
      logger.error(s"Giving up with $error after $totalRetries retries")
  }
}
