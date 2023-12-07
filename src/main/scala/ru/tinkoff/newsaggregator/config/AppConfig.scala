package ru.tinkoff.newsaggregator.config

import ru.tinkoff.newsaggregator.config.newsclient.NewsClientConfiguration
import ru.tinkoff.newsaggregator.config.retry.RetryConfiguration

case class PostgresConfig(url: String, user: String, password: String, poolSize: Int)

case class HttpServer(port: Int)

case class AppConfig(
    database: PostgresConfig,
    http: HttpServer,
    api: NewsClientConfiguration,
    retry: RetryConfiguration,
)
