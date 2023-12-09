package ru.tinkoff.newsaggregator.repository

import cats.effect.IO
import cats.effect.std.UUIDGen
import cats.effect.testing.scalatest.AsyncIOSpec
import org.mockito.Mockito.when
import org.scalatest.EitherValues
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import ru.tinkoff.newsaggregator.api.client.retrying.RetryingNewsClient
import ru.tinkoff.newsaggregator.controller.news.examples.NewsAPIResponseExample.okAPIExample
import ru.tinkoff.newsaggregator.domain.news.News
import ru.tinkoff.newsaggregator.domain.news.NewsCategory.Science
import ru.tinkoff.newsaggregator.domain.news.NewsCountry.{de, us}
import ru.tinkoff.newsaggregator.service.NewsService
import ru.tinkoff.newsaggregator.utils.TestData.{creationRequest, newsId, testGetByIdExample}

import java.time.{LocalDate, ZoneId}
import java.util.UUID

class NewsServiceSpec
    extends AsyncFreeSpec
    with EitherValues
    with Matchers
    with MockitoSugar
    with AsyncIOSpec {
  "news-api" - {
    "getByKeyWord" - {
      "return correct some news API Response" in {

        val mockNewsRepo = mock[NewsRepository[IO]]
        val mockNewsClient = mock[RetryingNewsClient[IO]]

        val service = NewsService.make[IO](mockNewsRepo, mockNewsClient)
        val keyWord = "Insider"
        val expected = Some(okAPIExample)

        when(mockNewsClient.getByKeyWord(keyWord)).thenReturn(IO(expected))

        val result = service.getByKeyWord(keyWord)

        result.asserting(_ shouldEqual expected)
      }

      "return None of API Response" in {
        val mockNewsRepo = mock[NewsRepository[IO]]
        val mockNewsClient = mock[RetryingNewsClient[IO]]

        val service = NewsService.make[IO](mockNewsRepo, mockNewsClient)
        val keyWord = "Insider"
        val expected = None

        when(mockNewsClient.getByKeyWord(keyWord)).thenReturn(IO(expected))

        val result = service.getByKeyWord(keyWord)

        result.asserting(_ shouldEqual expected)
      }
    }

    "getHeadlinesByCategory" - {
      "return correct some news API Response" in {
        val mockNewsRepo = mock[NewsRepository[IO]]
        val mockNewsClient = mock[RetryingNewsClient[IO]]

        val service = NewsService.make[IO](mockNewsRepo, mockNewsClient)
        val category = Science
        val expected = Some(okAPIExample)

        when(mockNewsClient.getHeadlinesByCategory(category)).thenReturn(IO(expected))

        val result = service.getHeadlinesByCategory(category)

        result.asserting(_ shouldEqual expected)
      }

      "return None of API Response" in {
        val mockNewsRepo = mock[NewsRepository[IO]]
        val mockNewsClient = mock[RetryingNewsClient[IO]]

        val service = NewsService.make[IO](mockNewsRepo, mockNewsClient)
        val category = Science
        val expected = None

        when(mockNewsClient.getHeadlinesByCategory(category)).thenReturn(IO(expected))

        val result = service.getHeadlinesByCategory(category)

        result.asserting(_ shouldEqual expected)
      }
    }

    "getHeadlinesByCountry" - {
      "return correct some news API Response" in {
        val mockNewsRepo = mock[NewsRepository[IO]]
        val mockNewsClient = mock[RetryingNewsClient[IO]]

        val service = NewsService.make[IO](mockNewsRepo, mockNewsClient)
        val country = us
        val expected = Some(okAPIExample)

        when(mockNewsClient.getHeadlinesByCountry(country)).thenReturn(IO(expected))

        val result = service.getHeadlinesByCountry(country)

        result.asserting(_ shouldEqual expected)
      }

      "return None of API Response" in {
        val mockNewsRepo = mock[NewsRepository[IO]]
        val mockNewsClient = mock[RetryingNewsClient[IO]]

        val service = NewsService.make[IO](mockNewsRepo, mockNewsClient)
        val country = de
        val expected = None

        when(mockNewsClient.getHeadlinesByCountry(country)).thenReturn(IO(expected))

        val result = service.getHeadlinesByCountry(country)

        result.asserting(_ shouldEqual expected)
      }
    }
  }

  "db" - {
    "create" - {
      "return news response" in {
        val mockNewsRepo = mock[NewsRepository[IO]]
        val mockNewsClient = mock[RetryingNewsClient[IO]]
        implicit val uuidGen: UUIDGen[IO] = mock[UUIDGen[IO]] // works only in mock
        val id = UUID.randomUUID()

        when(uuidGen.randomUUID).thenReturn(IO.pure(id))

        val service = NewsService.make[IO](mockNewsRepo, mockNewsClient)
        val request = creationRequest
        val news = News.fromCreateNews(id, request)

        when(mockNewsRepo.create(news)).thenReturn(IO(1L))

        val result = service.create(creationRequest)

        result.asserting(_ shouldEqual news.toResponse)
      }
    }

    "get" - {
      "return news response" in {
        val mockNewsRepo = mock[NewsRepository[IO]]
        val mockNewsClient = mock[RetryingNewsClient[IO]]

        val service = NewsService.make[IO](mockNewsRepo, mockNewsClient)

        when(mockNewsRepo.get(newsId)).thenReturn(IO(Some(testGetByIdExample.toNews)))

        val result = service.get(newsId)

        result.asserting(_ shouldEqual Some(testGetByIdExample))
      }

      "return none" in {
        val mockNewsRepo = mock[NewsRepository[IO]]
        val mockNewsClient = mock[RetryingNewsClient[IO]]

        val service = NewsService.make[IO](mockNewsRepo, mockNewsClient)

        when(mockNewsRepo.get(newsId)).thenReturn(IO(None))

        val result = service.get(newsId)

        result.asserting(_ shouldEqual None)
      }
    }

    "getByKeyWord" - {
      "return news response" in {
        val mockNewsRepo = mock[NewsRepository[IO]]
        val mockNewsClient = mock[RetryingNewsClient[IO]]

        val service = NewsService.make[IO](mockNewsRepo, mockNewsClient)

        val keyWord = "Insider"

        when(mockNewsRepo.getByKeyWord(keyWord)).thenReturn(IO(List(testGetByIdExample.toNews)))

        val result = service.getByKeyWordFromDB(keyWord)

        result.asserting(_ shouldEqual List(testGetByIdExample))
      }

      "return none" in {
        val mockNewsRepo = mock[NewsRepository[IO]]
        val mockNewsClient = mock[RetryingNewsClient[IO]]

        val service = NewsService.make[IO](mockNewsRepo, mockNewsClient)

        val keyWord = "Insider"

        when(mockNewsRepo.getByKeyWord(keyWord)).thenReturn(IO(List.empty))

        val result = service.getByKeyWordFromDB(keyWord)

        result.asserting(_ shouldEqual List.empty)
      }
    }

    "listByDate" - {
      val start = LocalDate.parse("2023-12-11").atStartOfDay(ZoneId.systemDefault())
      val end = LocalDate.parse("2023-12-12").atStartOfDay(ZoneId.systemDefault())
      val mockNewsRepo = mock[NewsRepository[IO]]
      val mockNewsClient = mock[RetryingNewsClient[IO]]

      val service = NewsService.make[IO](mockNewsRepo, mockNewsClient)

      "return news response" in {
        when(mockNewsRepo.listByDate(start, end)).thenReturn(IO(List(testGetByIdExample.toNews)))
        val result = service.listByDate(start, end)

        result.asserting(_ shouldEqual List(testGetByIdExample))
      }

      "return none" in {
        when(mockNewsRepo.listByDate(start, end)).thenReturn(IO(List.empty))

        val result = service.listByDate(start, end)

        result.asserting(_ shouldEqual List.empty)
      }
    }

  }
}
