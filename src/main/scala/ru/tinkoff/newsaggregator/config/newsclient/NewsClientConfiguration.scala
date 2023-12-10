package ru.tinkoff.newsaggregator.config.newsclient

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus.toFicusConfig
import net.ceedubs.ficus.readers.ValueReader

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

case class NewsClientConfiguration(url: String, timeout: FiniteDuration, key: String)

object NewsClientConfiguration {
  def load(config: Config): NewsClientConfiguration =
    config.as[NewsClientConfiguration]

  private implicit val newsClientConfigurationReader: ValueReader[NewsClientConfiguration] =
    ValueReader.relative(config =>
      NewsClientConfiguration(
        url = config.getString("url"),
        timeout = FiniteDuration.apply(config.getLong("timeout"), TimeUnit.MILLISECONDS),
        key = config.getString("key"),
      ),
    )
}
