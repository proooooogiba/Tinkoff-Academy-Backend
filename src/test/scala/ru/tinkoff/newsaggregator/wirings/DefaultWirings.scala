package ru.tinkoff.newsaggregator.wirings

import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader
import ru.tinkoff.newsaggregator.config.AppConfig
import ru.tinkoff.newsaggregator.config.newsclient.NewsClientConfiguration
import ru.tinkoff.newsaggregator.config.retry.RetryConfiguration
import ru.tinkoff.newsaggregator.utils.TestUtils

trait DefaultWirings extends TestUtils {
  val config: AppConfig =
    ConfigSource.default.loadOrThrow[AppConfig]
  val newsClientConfiguration: NewsClientConfiguration = config.api
  val retryConfiguration: RetryConfiguration = config.retry
}
