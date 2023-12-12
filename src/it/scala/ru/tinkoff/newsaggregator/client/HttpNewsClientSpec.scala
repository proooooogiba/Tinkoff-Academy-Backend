package ru.tinkoff.newsaggregator.client

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.softwaremill.macwire.wire
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.newsaggregator.api.client.http.HttpNewsClient
import ru.tinkoff.newsaggregator.controller.news.examples.NewsAPIResponseExample.okAPIExample
import ru.tinkoff.newsaggregator.domain.news.NewsCategory.Science
import ru.tinkoff.newsaggregator.domain.news.NewsCountry.cz
import ru.tinkoff.newsaggregator.domain.news.response.NewsAPIResponse
import ru.tinkoff.newsaggregator.wirings.DefaultWirings

class HttpOrderClientSpec
    extends AsyncFreeSpec
    with AsyncIOSpec
    with Matchers
    with NewsClientImplSpecUtils {

  val alternative: NewsAPIResponse = NewsAPIResponse("error", 0, List.empty)

  "getByKeyWord" - {
    "get api response by key word" in {
      val keyWord = "Insider"
      for {
        _ <- client
          .getByKeyWord(keyWord)
          .asserting(_.getOrElse(alternative) shouldBe okAPIExample)
      } yield ()
    }
  }

  "getHeadlinesByCategory" - {
    "get api response by headlines of category" in {
      val category = Science
      for {
        _ <- client
          .getHeadlinesByCategory(category)
          .asserting(_.getOrElse(alternative) shouldBe okAPIExample)
      } yield ()
    }
  }

  "getHeadlinesByCountry" - {
    "get api response by headlines of category" in {
      val country = cz
      for {
        _ <- client
          .getHeadlinesByCountry(country)
          .asserting(_.getOrElse(alternative) shouldBe okAPIExample)
      } yield ()
    }
  }

}

trait NewsClientImplSpecUtils extends DefaultWirings {
  val client: HttpNewsClient[IO] = wire[HttpNewsClient[IO]]
}
