package ru.tinkoff.petstore.config

case class PostgresConfig(url: String, user: String, password: String, poolSize: Int)

case class HttpServer(port: Int)

case class AppConfig(database: PostgresConfig, http: HttpServer)
