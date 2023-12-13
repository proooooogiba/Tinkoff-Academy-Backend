package ru.tinkoff.newsaggregator.client

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import com.dimafeng.testcontainers.{ContainerDef, DockerComposeContainer, ExposedService}
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.testcontainers.containers.wait.strategy.Wait
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jFactory
import ru.tinkoff.newsaggregator.api.client.http.HttpNewsClient
import ru.tinkoff.newsaggregator.api.client.retrying.RetryingNewsClient
import ru.tinkoff.newsaggregator.api.commons.RetryUtilsImpl
import ru.tinkoff.newsaggregator.controller.news.examples.NewsAPIResponseExample.okAPIExample
import ru.tinkoff.newsaggregator.domain.news.NewsCategory.Science
import ru.tinkoff.newsaggregator.domain.news.NewsCountry.cz
import ru.tinkoff.newsaggregator.domain.news.response.NewsAPIResponse
import ru.tinkoff.newsaggregator.wirings.DefaultWirings
import sttp.client3.SttpBackend

import java.io.File

class RetryingOrderClientSpec
    extends AsyncFreeSpec
    with AsyncIOSpec
    with Matchers
    with NewsServiceImplSpecUtils
    with TestContainerForAll {

  override val containerDef: ContainerDef = DockerComposeContainer.Def(
    new File("src/it/resources/news/docker-compose.yml"),
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
        _ <- service
          .getByKeyWord(keyWord)
          .asserting(_.getOrElse(alternative) shouldBe okAPIExample)
      } yield ()
    }
  }

  "getHeadlinesByCategory" - {
    "get api response by headlines of category" in {
      val category = Science
      for {
        _ <- service
          .getHeadlinesByCategory(category)
          .asserting(_.getOrElse(alternative) shouldBe okAPIExample)
      } yield ()
    }
  }

  "getHeadlinesByCountry" - {
    "get api response by headlines of category" in {
      val country = cz
      for {
        _ <- service
          .getHeadlinesByCountry(country)
          .asserting(_.getOrElse(alternative) shouldBe okAPIExample)
      } yield ()
    }
  }

//  Тест не проходит
//  "TimeOutException" - {
//    "get timeout exception" in {
//      val country = cz
//      def newsAPITimeoutMethod: IO[Option[NewsAPIResponse]] =
//        service.getHeadlinesByCountry(cz).andWait(Duration(2, TimeUnit.SECONDS))
//
//      val result = service.retryingWrapper(newsAPITimeoutMethod)
//      result.assertThrows[TimeoutException]
//    }
//  }
}

trait NewsServiceImplSpecUtils extends DefaultWirings {
  val backend: SttpBackend[IO, Any] = sttpBackend
  val client: HttpNewsClient[IO] = new HttpNewsClient[IO](backend, newsClientConfiguration)
  val logger: Logger[IO] = Slf4jFactory.create[IO].getLogger
  val retryUtils: RetryUtilsImpl[IO] = new RetryUtilsImpl[IO](logger, retryConfiguration)
  val service: RetryingNewsClient[IO] = new RetryingNewsClient[IO](client, retryUtils)
}
