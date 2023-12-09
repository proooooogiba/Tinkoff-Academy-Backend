package ru.tinkoff.newsaggregator.controller

import _root_.ru.tinkoff.newsaggregator.service.NewsService
import _root_.ru.tinkoff.newsaggregator.utils.TestData._
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.mockito.Mockito.when
import org.scalatest.EitherValues
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import ru.tinkoff.newsaggregator.controller.news.ControllerErrors.{
  resourceNotFoundByKeyWord,
  resourceNotFoundDel,
  resourceNotFoundError,
  resourceNotFoundGet,
}
import ru.tinkoff.newsaggregator.controller.news.examples.NewsAPIResponseExample.{
  errorAPIExample,
  notFoundAPIExample,
  okAPIExample,
}
import ru.tinkoff.newsaggregator.controller.news.{
  NewsController,
  ResourceNotFound,
  ServerError,
  UserBadRequest,
}
import ru.tinkoff.newsaggregator.domain.news.News
import ru.tinkoff.newsaggregator.domain.news.NewsCategory.{Business, Technology}
import ru.tinkoff.newsaggregator.domain.news.NewsCountry.ru
import sttp.client3.testing.SttpBackendStub
import sttp.client3.{SttpBackend, UriContext, basicRequest}
import sttp.model.StatusCode
import sttp.model.StatusCode.{InternalServerError, NotFound, Ok}
import sttp.tapir.integ.cats.effect.CatsMonadError
import sttp.tapir.server.stub.TapirStubInterpreter
import tethys.JsonObjectWriter.lowPriorityWriter
import tethys.JsonWriterOps
import tethys.derivation.auto.jsonWriterMaterializer
import tethys.jackson.jacksonTokenWriterProducer

import java.time.{LocalDate, ZoneId}
import scala.language.postfixOps

class NewsControllerSpec
    extends AsyncFreeSpec
    with EitherValues
    with Matchers
    with MockitoSugar
    with AsyncIOSpec {
  "db" - {
    "save" - {
      "return correct response" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).createNews)
            .backend()

        val expectedResponse = News.fromCreateNews(newsId, creationRequest).toResponse
        when(mockService.create(creationRequest)).thenReturn(IO(expectedResponse))

        val response = basicRequest
          .post(uri"http://localhost:8080/api/v1/db/save")
          .body(creationRequest.asJson)
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe Ok)
          _ <- response.asserting(_.body.isRight shouldBe true)
          _ <- response.asserting(_.body shouldBe Right(expectedResponse.asJson))
        } yield ()
      }
    }

    "getById" - {
      "return news with existing Id" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).getNewsById)
            .backend()

        when(mockService.get(newsId)).thenReturn(IO(Some(testGetByIdExample)))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/db/get/$newsId")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe Ok)
          _ <- response.asserting(_.body.isRight shouldBe true)
          _ <- response.asserting(_.body shouldBe Right(testGetByIdExample.asJson))
        } yield ()
      }

      "return none because Id doesn't exist" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).getNewsById)
            .backend()

        when(mockService.get(newsId)).thenReturn(IO(None))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/db/get/$newsId")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe NotFound)
          _ <- response.asserting(_.body.isLeft shouldBe true)
          _ <- response.asserting(_.body shouldBe Left(resourceNotFoundGet.asJson))
        } yield ()
      }
    }

    "deleteById" - {
      "delete news with existing Id" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).deleteNews)
            .backend()

        when(mockService.delete(newsId2)).thenReturn(IO(Some(testDeleteByIdExample)))

        val response = basicRequest
          .delete(uri"http://localhost:8080/api/v1/db/delete/$newsId2")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe Ok)
          _ <- response.asserting(_.body.isRight shouldBe true)
          _ <- response.asserting(_.body shouldBe Right(testDeleteByIdExample.asJson))
        } yield ()
      }

      "return error because Id doesn't exist" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).deleteNews)
            .backend()

        when(mockService.delete(newsId2)).thenReturn(IO(None))

        val response = basicRequest
          .delete(uri"http://localhost:8080/api/v1/db/delete/$newsId2")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe NotFound)
          _ <- response.asserting(_.body.isLeft shouldBe true)
          _ <- response.asserting(_.body shouldBe Left(resourceNotFoundDel.asJson))
        } yield ()
      }
    }

    "all" - {
      "return all existing news" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).allNews)
            .backend()

        when(mockService.list).thenReturn(IO(testAllExample))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/db/all")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe Ok)
          _ <- response.asserting(_.body.isRight shouldBe true)
          _ <- response.asserting(_.body shouldBe Right(testAllExample.asJson))
        } yield ()
      }

      "return error, because there isn't any existing news" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).allNews)
            .backend()

        when(mockService.list).thenReturn(IO(List.empty))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/db/all")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe NotFound)
          _ <- response.asserting(_.body.isLeft shouldBe true)
          _ <- response.asserting(_.body shouldBe Left(resourceNotFoundError.asJson))
        } yield ()
      }
    }

    "getByKeyWord" - {
      "return news with key word from db" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).getByKeyWordFromDB)
            .backend()

        val keyWord = "Insider"

        when(mockService.getByKeyWordFromDB(keyWord))
          .thenReturn(IO(List(testGetByKeyWordExample)))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/db/keyWord/$keyWord")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe Ok)
          _ <- response.asserting(_.body.isRight shouldBe true)
          _ <- response.asserting(_.body shouldBe Right(List(testGetByKeyWordExample).asJson))
        } yield ()
      }

      "return error, because there isn't any existing news" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).getByKeyWordFromDB)
            .backend()

        val keyWord = "Bitcoin"

        when(mockService.getByKeyWordFromDB(keyWord)).thenReturn(IO(List.empty))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/db/keyWord/$keyWord")
          .send(backendStub)

        for {
          _ <- response.asserting(_.body.isLeft shouldBe true)
          _ <- response.asserting(_.body.isLeft shouldBe true)
          _ <- response.asserting(_.body shouldBe Left(resourceNotFoundByKeyWord.asJson))
        } yield ()
      }
    }

    "getByPublishedRange" - {
      val start = LocalDate.parse("2023-11-10")
      val end = LocalDate.parse("2023-12-12")
      val zStart = start.atStartOfDay(ZoneId.systemDefault())
      val zEnd = end.atStartOfDay(ZoneId.systemDefault())

      "return news by date range from db" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(
              NewsController.make[IO](mockService).getNewsByPublishedRange,
            )
            .backend()

        when(mockService.listByDate(zStart, zEnd))
          .thenReturn(IO(List(testGetByKeyWordExample)))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/db/dateRange/$start/$end")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe Ok)
          _ <- response.asserting(_.body.isRight shouldBe true)
          _ <- response.asserting(_.body shouldBe Right(List(testGetByKeyWordExample).asJson))
        } yield ()

      }

      "return error, because there isn't any news in data range" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(
              NewsController.make[IO](mockService).getNewsByPublishedRange,
            )
            .backend()

        when(mockService.listByDate(zStart, zEnd)).thenReturn(IO(List.empty))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/db/dateRange/$start/$end")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe NotFound)
          _ <- response.asserting(_.body.isLeft shouldBe true)
          _ <- response.asserting(
            _.body shouldBe Left(
              Right(
                ResourceNotFound("Сохранённые новости в данном диапазоне не найдены"),
              ).asJson,
            ),
          )
        } yield ()
      }

      "return error, because start date after end date" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(
              NewsController.make[IO](mockService).getNewsByPublishedRange,
            )
            .backend()

        val start = LocalDate.parse("2023-12-15")
        val end = LocalDate.parse("2023-12-12")

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/db/dateRange/$start/$end")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe StatusCode(400))
          _ <- response.asserting(_.body.isLeft shouldBe true)
          _ <- response.asserting(
            _.body shouldBe Left(
              Right(
                UserBadRequest("Начальная дата не может быть после конечной даты"),
              ).asJson,
            ),
          )
        } yield ()
      }
    }
  }

  "news-api" - {
    "getNewsByKeyWord" - {
      "return news with key word" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).getNewsByKeyWord)
            .backend()

        val keyWord = "Insider"

        when(mockService.getByKeyWord(keyWord))
          .thenReturn(IO(Some(okAPIExample)))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/news-api/keyWord/$keyWord")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe Ok)
          _ <- response.asserting(_.body.isRight shouldBe true)
          _ <- response.asserting(_.body shouldBe Right(okAPIExample.asJson))
        } yield ()
      }

      "return response with no results" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).getNewsByKeyWord)
            .backend()

        val keyWord = "Insider"

        when(mockService.getByKeyWord(keyWord))
          .thenReturn(IO(Some(notFoundAPIExample)))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/news-api/keyWord/$keyWord")
          .send(backendStub)

        println(response.unsafeRunSync())

        for {
          _ <- response.asserting(_.code shouldEqual NotFound)
          _ <- response.asserting(_.body.isLeft shouldBe true)
          _ <- response.asserting(
            _.body shouldBe Left(
              Right(ResourceNotFound(s"Новости по ключевому слову $keyWord не были найдены")).asJson,
            ),
          )
        } yield ()
      }

      "return error because of internal server error" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).getNewsByKeyWord)
            .backend()

        val keyWord = "Insider"

        when(mockService.getByKeyWord(keyWord))
          .thenReturn(IO(None))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/news-api/keyWord/$keyWord")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe InternalServerError)
          _ <- response.asserting(_.body.isLeft shouldBe true)
          _ <- response.asserting(
            _.body shouldBe Left(
              Left(
                ServerError(
                  s"Проблема отправки запроса на внешний сервис:" +
                    s" убедитесь, что запрос отправляемый на News.org правильный",
                ),
              ).asJson,
            ),
          )
        } yield ()
      }
    }

    "getHeadlinesByCategory" - {
      "return news by category" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).getHeadlinesByCategory)
            .backend()

        when(mockService.getHeadlinesByCategory(Business))
          .thenReturn(IO(Some(okAPIExample)))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/news-api/category/$Business")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe Ok)
          _ <- response.asserting(_.body.isRight shouldBe true)
          _ <- response.asserting(_.body shouldBe Right(okAPIExample.asJson))
        } yield ()

      }

      "handel internal server error" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).getHeadlinesByCategory)
            .backend()

        when(mockService.getHeadlinesByCategory(Technology))
          .thenReturn(IO(None))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/news-api/category/$Technology")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldEqual InternalServerError)
          _ <- response.asserting(_.body.isLeft shouldBe true)
          _ <- response.asserting(
            _.body shouldBe Left(
              Left(
                ServerError(
                  s"Проблема отправки запроса на внешний сервис:" +
                    s" убедитесь, что запрос отправляемый на News.org правильный",
                ),
              ).asJson,
            ),
          )
        } yield ()

      }

      "handel internal api error" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).getHeadlinesByCategory)
            .backend()

        when(mockService.getHeadlinesByCategory(Technology))
          .thenReturn(IO(Some(errorAPIExample)))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/news-api/category/$Technology")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldEqual InternalServerError)
          _ <- response.asserting(_.body.isLeft shouldBe true)
          _ <- response.asserting(
            _.body shouldBe Left(
              Left(ServerError(s"Ошибка на стороне внешнего сервиса")).asJson,
            ),
          )
        } yield ()
      }
    }

    "getHeadlinesByCountry" - {
      "return news by country" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).getHeadlinesByCountry)
            .backend()

        when(mockService.getHeadlinesByCountry(ru))
          .thenReturn(IO(Some(okAPIExample)))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/news-api/country/$ru")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe Ok)
          _ <- response.asserting(_.body.isRight shouldBe true)
          _ <- response.asserting(_.body shouldBe Right(okAPIExample.asJson))
        } yield ()
      }

      "handel internal server error" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).getHeadlinesByCountry)
            .backend()

        when(mockService.getHeadlinesByCountry(ru))
          .thenReturn(IO(None))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/news-api/country/$ru")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldEqual InternalServerError)
          _ <- response.asserting(_.body.isLeft shouldBe true)
          _ <- response.asserting(
            _.body shouldBe Left(
              Left(
                ServerError(
                  s"Проблема отправки запроса на внешний сервис:" +
                    s" убедитесь, что запрос отправляемый на News.org правильный",
                ),
              ).asJson,
            ),
          )
        } yield ()
      }

      "handel internal api error" in {
        val mockService = mock[NewsService[IO]]

        val backendStub: SttpBackend[IO, Any] =
          TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
            .whenServerEndpointRunLogic(NewsController.make[IO](mockService).getHeadlinesByCountry)
            .backend()

        when(mockService.getHeadlinesByCountry(ru))
          .thenReturn(IO(Some(errorAPIExample)))

        val response = basicRequest
          .get(uri"http://localhost:8080/api/v1/news-api/country/$ru")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldEqual InternalServerError)
          _ <- response.asserting(_.body.isLeft shouldBe true)
          _ <- response.asserting(
            _.body shouldBe Left(
              Left(ServerError(s"Ошибка на стороне внешнего сервиса")).asJson,
            ),
          )
        } yield ()
      }
    }
  }
}
