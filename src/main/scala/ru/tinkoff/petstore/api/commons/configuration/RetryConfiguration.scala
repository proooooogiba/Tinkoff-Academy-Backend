package ru.tinkoff.petstore.api.commons.configuration

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus.toFicusConfig
import net.ceedubs.ficus.readers.ValueReader

import scala.concurrent.duration.{FiniteDuration, SECONDS}

case class RetryConfiguration(duration: FiniteDuration, amount: Int)

object RetryConfiguration {
  def load(config: Config): RetryConfiguration =
    config.as[RetryConfiguration]

  private implicit val newsClientConfigurationReader: ValueReader[RetryConfiguration] =
    ValueReader.relative(config =>
      RetryConfiguration(
        duration = FiniteDuration.apply(config.getLong("duration"), SECONDS),
        amount = config.getInt("amount"),
      ),
    )
}
