package ru.tinkoff.petstore.config

import ru.tinkoff.petstore.api.commons.configuration.RetryConfiguration
import ru.tinkoff.petstore.api.news.model.configuration.NewsClientConfiguration

case class PostgresConfig(url: String, user: String, password: String, poolSize: Int)

case class HttpServer(port: Int)

case class AppConfig(
    database: PostgresConfig,
    http: HttpServer,
    api: NewsClientConfiguration,
    retry: RetryConfiguration,
)
