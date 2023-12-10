package ru.tinkoff.newsaggregator.client

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import com.dimafeng.testcontainers.{ContainerDef, DockerComposeContainer, ExposedService}
import org.asynchttpclient.DefaultAsyncHttpClient
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.testcontainers.containers.wait.strategy.Wait
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jFactory
import ru.tinkoff.newsaggregator.api.client.http.HttpNewsClient
import ru.tinkoff.newsaggregator.api.client.retrying.RetryingNewsClient
import ru.tinkoff.newsaggregator.api.commons.RetryUtilsImpl
import ru.tinkoff.newsaggregator.domain.news.NewsCategory.Science
import ru.tinkoff.newsaggregator.domain.news.NewsCountry.cz
import ru.tinkoff.newsaggregator.domain.news.response.NewsAPIResponse
import ru.tinkoff.newsaggregator.wirings.DefaultWirings
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend

import java.io.File

class RetryingOrderClientSpec
    extends AsyncFreeSpec
    with AsyncIOSpec
    with Matchers
    with OrderServiceImplSpecUtils
    with TestContainerForAll {

  override val containerDef: ContainerDef = DockerComposeContainer.Def(
    new File("src/test/resources/news/docker-compose.yml"),
    tailChildContainers = true,
    exposedServices = Seq(
      ExposedService("wiremock", 8080, Wait.forListeningPort()),
    ),
  )
  val alternative: NewsAPIResponse = NewsAPIResponse("error", 0, List.empty)

  "getByKeyWord" - {
    "get api response by key word" in {
      val keyWord = "Insider"
      for {
        _ <- client
          .getByKeyWord(keyWord)
          .asserting(_.getOrElse(alternative).status shouldBe "ok")
        _ <- client
          .getByKeyWord(keyWord)
          .asserting(_.getOrElse(alternative).totalResults shouldNot be(0))
        _ <- client
          .getByKeyWord(keyWord)
          .asserting(_.getOrElse(alternative).articles shouldNot be(List.empty))
      } yield ()
    }

    "get zero results by key word" in {
      val invalidKeyWord = "абауцлаьцдл"
      for {
        _ <- client
          .getByKeyWord(invalidKeyWord)
          .asserting(_.getOrElse(alternative).status shouldBe "ok")
        _ <- client
          .getByKeyWord(invalidKeyWord)
          .asserting(_.getOrElse(alternative).totalResults shouldBe 0)
        _ <- client
          .getByKeyWord(invalidKeyWord)
          .asserting(_.getOrElse(alternative).articles shouldBe List.empty)
      } yield ()
    }
  }

  "getHeadlinesByCategory" - {
    "get api response by headlines of category" in {
      val category = Science
      for {
        _ <- client
          .getHeadlinesByCategory(category)
          .asserting(_.getOrElse(alternative).status shouldBe "ok")
        _ <- client
          .getHeadlinesByCategory(category)
          .asserting(
            _.getOrElse(alternative).totalResults shouldNot be(0),
          )
        _ <- client
          .getHeadlinesByCategory(category)
          .asserting(_.getOrElse(alternative).totalResults shouldNot be(List.empty))
      } yield ()
    }
  }

  "getHeadlinesByCountry" - {
    "get api response by headlines of category" in {
      val country = cz
      for {
        _ <- client
          .getHeadlinesByCountry(country)
          .asserting(_.getOrElse(alternative).status shouldBe "ok")
        _ <- client
          .getHeadlinesByCountry(country)
          .asserting(_.getOrElse(alternative).totalResults shouldNot be(0))
        _ <- client
          .getHeadlinesByCountry(country)
          .asserting(_.getOrElse(alternative).totalResults shouldNot be(List.empty))
      } yield ()
    }
  }
}

trait OrderServiceImplSpecUtils extends DefaultWirings {
  val backend: SttpBackend[IO, Any] =
    AsyncHttpClientCatsBackend.usingClient[IO](new DefaultAsyncHttpClient())
  val client: HttpNewsClient[IO] = new HttpNewsClient[IO](backend, newsClientConfiguration)
  val logger: Logger[IO] = Slf4jFactory.create[IO].getLogger
  val retryUtils: RetryUtilsImpl[IO] = new RetryUtilsImpl[IO](logger, retryConfiguration)
  val service: RetryingNewsClient[IO] = new RetryingNewsClient[IO](client, retryUtils)
}
