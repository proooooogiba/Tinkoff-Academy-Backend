package ru.tinkoff.petstore.api.news.model.configuration

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus.toFicusConfig
import net.ceedubs.ficus.readers.ValueReader

import scala.concurrent.duration.{FiniteDuration, SECONDS}

case class NewsClientConfiguration(url: String, timeout: FiniteDuration, key: String)

object NewsClientConfiguration {
  def load(config: Config): NewsClientConfiguration =
    config.as[NewsClientConfiguration]

  private implicit val newsClientConfigurationReader: ValueReader[NewsClientConfiguration] =
    ValueReader.relative(config =>
      NewsClientConfiguration(
        url = config.getString("url"),
        timeout = FiniteDuration.apply(config.getLong("timeout"), SECONDS),
        key = config.getString("key"),
      ),
    )
}
