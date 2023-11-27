package ru.tinkoff.petstore.repository.postgresql

import cats.effect.testing.scalatest.{AsyncIOSpec, CatsResourceIO}
import cats.effect.{IO, Resource}
import cats.implicits.catsSyntaxOptionId
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
import ru.tinkoff.petstore.config.PostgresConfig
import ru.tinkoff.petstore.database.FlywayMigration
import ru.tinkoff.petstore.domain.pet.{Pet, PetCategory}
import ru.tinkoff.petstore.database.transactor.makeTransactor

import java.util.UUID

class PetsRepositorySpec
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

  "PetsRepositoryPostgresql" should "return None if db is empty" in { implicit t =>
    val repo = new PetsRepositoryPostgresql[IO]
    val uuid = UUID.randomUUID()

    for {
      _ <- repo.list.asserting(_ shouldBe List.empty)
      _ <- repo.get(uuid).asserting(_ shouldBe None)
      _ <- repo.delete(uuid).asserting(_ shouldBe None)
    } yield ()
  }

  it should "insert pet" in { implicit t =>
    val repo = new PetsRepositoryPostgresql[IO]
    val pet = Pet(UUID.randomUUID(), "~~~", PetCategory.Worm, "...")

    for {
      _ <- repo.create(pet).asserting(_ shouldBe 1)
      _ <- repo.get(pet.id).asserting(_ shouldBe pet.some)
      _ <- repo.delete(pet.id).asserting(_ shouldBe pet.some)
      _ <- repo.delete(pet.id).asserting(_ shouldBe None)
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
