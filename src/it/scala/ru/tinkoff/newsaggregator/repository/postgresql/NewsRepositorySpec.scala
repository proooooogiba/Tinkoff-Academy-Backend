package ru.tinkoff.newsaggregator.repository.postgresql

import cats.effect.testing.scalatest.{AsyncIOSpec, CatsResourceIO}
import cats.effect.{IO, Resource}
import com.dimafeng.testcontainers.PostgreSQLContainer
import doobie.Transactor
import org.scalatest.flatspec.FixtureAsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import org.testcontainers.containers.wait.strategy.{
  LogMessageWaitStrategy,
  Wait,
  WaitAllStrategy,
  WaitStrategy,
}
import org.testcontainers.utility.DockerImageName
import ru.tinkoff.newsaggregator.config.PostgresConfig
import ru.tinkoff.newsaggregator.database.FlywayMigration
import ru.tinkoff.newsaggregator.database.transactor.makeTransactor
import ru.tinkoff.newsaggregator.domain.news.News
import ru.tinkoff.newsaggregator.utils.TestData.{
  creationRequest,
  testGetByKeyWordExample,
  testNotGetByKeyWordExample,
}

import java.time.{LocalDate, ZoneId}
import java.util.UUID

class NewsRepositorySpec
    extends FixtureAsyncFlatSpec
    with AsyncIOSpec
    with CatsResourceIO[Transactor[IO]]
    with Matchers {

  override val resource: Resource[IO, Transactor[IO]] =
    for {
      c <- containerResource
      conf = PostgresConfig(
        c.jdbcUrl,
        user = c.username,
        password = c.password,
        poolSize = 2,
      )
      _ <- Resource.eval(FlywayMigration.migrate[IO](conf))
      tx <- makeTransactor[IO](conf)
    } yield tx

  "NewsRepositoryPostgresql" should "return None if db is empty" in { implicit t =>
    val repo = new NewsRepositoryPostgresql[IO]
    val uuid = UUID.randomUUID()

    for {
      _ <- repo.list.asserting(_ shouldBe List.empty)
      _ <- repo.get(uuid).asserting(_ shouldBe None)
      _ <- repo.delete(uuid).asserting(_ shouldBe None)
    } yield ()
  }

  it should "insert news" in { implicit t =>
    val repo = new NewsRepositoryPostgresql[IO]
    val id = UUID.randomUUID()
    val news = News.fromCreateNews(id, creationRequest)

    for {
      _ <- repo.create(news).asserting(_ shouldBe 1)
      _ <- repo.get(news.id).asserting(_ shouldBe Some(news))
      _ <- repo.delete(news.id).asserting(_ shouldBe Some(news))
      _ <- repo.delete(news.id).asserting(_ shouldBe None)
    } yield ()
  }

  it should "get by keyWord" in { implicit t =>
    val repo = new NewsRepositoryPostgresql[IO]
    val news = testGetByKeyWordExample.toNews
    val keyWord = "newsletter"
    val another_news = testNotGetByKeyWordExample.toNews

    for {
      _ <- repo.create(news).asserting(_ shouldBe 1)
      _ <- repo.create(another_news).asserting(_ shouldBe 1)
      _ <- repo.get(news.id).asserting(_ shouldBe Some(news))
      _ <- repo.get(another_news.id).asserting(_ shouldBe Some(another_news))
      _ <- repo.getByKeyWord(keyWord).asserting(_ shouldBe List(news))
      _ <- repo.delete(news.id).asserting(_ shouldBe Some(news))
      _ <- repo.getByKeyWord(keyWord).asserting(_ shouldBe List.empty)
    } yield ()
  }

  it should "get by Date range" in { implicit t =>
    val repo = new NewsRepositoryPostgresql[IO]
    val news = testGetByKeyWordExample.toNews
    val start = LocalDate.parse("2023-11-09").atStartOfDay(ZoneId.systemDefault)
    val end = LocalDate.parse("2023-11-11").atStartOfDay(ZoneId.systemDefault)

    for {
      _ <- repo.create(news).asserting(_ shouldBe 1)
      _ <- repo.listByDate(start, end).asserting(_ shouldBe List(news))
      _ <- repo.delete(news.id).asserting(_ shouldBe Some(news))
      _ <- repo.listByDate(start, end).asserting(_ shouldBe List.empty)
    } yield ()
  }

  private val defaultWaitStrategy: WaitStrategy = new WaitAllStrategy()
    .withStrategy(Wait.forListeningPort())
    .withStrategy(
      new LogMessageWaitStrategy()
        .withRegEx(".*database system is ready to accept connections.*\\s")
        .withTimes(2),
    )

  private def containerResource: Resource[IO, PostgreSQLContainer] =
    Resource.make(
      IO {
        val c = PostgreSQLContainer
          .Def(
            dockerImageName = DockerImageName.parse("postgres:14.7"),
          )
          .start()
        c.container.waitingFor(defaultWaitStrategy)
        c
      },
    )(c => IO(c.stop()))
}
