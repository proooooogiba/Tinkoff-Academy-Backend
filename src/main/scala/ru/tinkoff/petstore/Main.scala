package ru.tinkoff.petstore

import cats.effect.{IO, IOApp}
import com.comcast.ip4s.{Host, Port}
import doobie.Transactor
import org.asynchttpclient.DefaultAsyncHttpClient
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.typelevel.log4cats.slf4j.Slf4jFactory
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import ru.tinkoff.petstore.api.commons.RetryUtilsImpl
import ru.tinkoff.petstore.api.news.HttpNewsClient
import ru.tinkoff.petstore.api.service.RetryingNewsClient
import ru.tinkoff.petstore.config.AppConfig
import ru.tinkoff.petstore.controller.{
  ExampleController,
  NewsController,
  OrderController,
  PetController,
}
import ru.tinkoff.petstore.database.FlywayMigration
import ru.tinkoff.petstore.database.transactor.makeTransactor
import ru.tinkoff.petstore.repository.postgresql.{
  NewsRepositoryPostgresql,
  OrderRepositoryPostgresql,
  PetsRepositoryPostgresql,
}
import ru.tinkoff.petstore.repository.{NewsRepository, OrderRepository, PetRepository}
import ru.tinkoff.petstore.service.{NewsService, OrderService, PetService}
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

object Main extends IOApp.Simple {
  override def run: IO[Unit] = {

    val conf = ConfigSource.default.loadOrThrow[AppConfig]

    val backend: SttpBackend[IO, Any] =
      AsyncHttpClientCatsBackend.usingClient[IO](new DefaultAsyncHttpClient())

    val client =
      new HttpNewsClient[IO](backend, conf.api)

    val logger = Slf4jFactory.create[IO].getLogger

    val retryUtils =
      new RetryUtilsImpl[IO](logger, conf.retry)

    val newsClient: RetryingNewsClient[IO] = new RetryingNewsClient[IO](client, retryUtils)

    makeTransactor[IO](conf.database).use { implicit xa: Transactor[IO] =>
      val orderRepo: OrderRepository[IO] = new OrderRepositoryPostgresql[IO]
      val petRepo: PetRepository[IO] = new PetsRepositoryPostgresql[IO]
      val newsRepo: NewsRepository[IO] = new NewsRepositoryPostgresql[IO]

      for {
        _ <- FlywayMigration.migrate[IO](conf.database)

        endpoints <- IO.delay {
          List(
            ExampleController.make[IO],
            OrderController.make(OrderService.make(orderRepo)),
            PetController.make(PetService.make(petRepo)),
            NewsController.make(NewsService.make(newsRepo, newsClient)),
          ).flatMap(_.endpoints)
        }

        swagger = SwaggerInterpreter()
          .fromServerEndpoints[IO](endpoints, "news-aggregator", "1.0.0")
        routes = Http4sServerInterpreter[IO]().toRoutes(swagger ++ endpoints)
        port <- IO.fromOption(Port.fromInt(conf.http.port))(new Exception("Invalid http port"))
        _ <- IO.println(s"Go to http://localhost:${conf.http.port}/docs to open SwaggerUI")

        _ <- EmberServerBuilder
          .default[IO]
          .withHost(Host.fromString("localhost").get)
          .withPort(port)
          .withHttpApp(Router("/" -> routes).orNotFound)
          .build
          .useForever
      } yield ()
    }
  }
}
