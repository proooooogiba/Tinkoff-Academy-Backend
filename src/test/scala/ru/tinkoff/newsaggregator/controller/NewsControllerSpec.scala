package ru.tinkoff.newsaggregator.controller

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.mockito.Mockito.when
import org.scalatest.EitherValues
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import ru.tinkoff.newsaggregator.service._
import ru.tinkoff.newsaggregator.controller.news.ControllerErrors._
import ru.tinkoff.newsaggregator.controller.news.NewsController
import ru.tinkoff.newsaggregator.controller.news.examples.NewsAPIResponseExample.{
  errorAPIExample,
  notFoundAPIExample,
  okAPIExample,
}
import ru.tinkoff.newsaggregator.utils.TestData._
import ru.tinkoff.newsaggregator.domain.news.News
import ru.tinkoff.newsaggregator.domain.news.NewsCategory.{Business, Technology}
import ru.tinkoff.newsaggregator.domain.news.NewsCountry.ru
import sttp.client3.testing.SttpBackendStub
import sttp.client3.{SttpBackend, UriContext, basicRequest}
import sttp.model.StatusCode
import sttp.model.StatusCode.{InternalServerError, NotFound, Ok, Unauthorized}
import sttp.tapir.integ.cats.effect.CatsMonadError
import sttp.tapir.model.UsernamePassword
import sttp.tapir.server.stub.TapirStubInterpreter
import tethys.JsonObjectWriter.lowPriorityWriter
import tethys.JsonWriterOps
import tethys.derivation.auto.jsonWriterMaterializer
import tethys.jackson.jacksonTokenWriterProducer

import java.time.{LocalDate, ZoneId}
import scala.language.postfixOps

class NewsControllerSpec
    extends AsyncFreeSpec
    with AsyncIOSpec
    with EitherValues
    with Matchers
    with MockitoSugar {
  "db" - {
    "save" - {
      val mockService = mock[NewsService[IO]]
      val mockUserService = mock[UserService[IO]]

      val backendStub: SttpBackend[IO, Any] =
        TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
          .whenServerEndpointRunLogic(
            NewsController.make[IO](mockService, mockUserService).createNews,
          )
          .backend()

      "return correct response" in {
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
      val mockService = mock[NewsService[IO]]
      val mockUserService = mock[UserService[IO]]

      val backendStub: SttpBackend[IO, Any] =
        TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
          .whenServerEndpointRunLogic(
            NewsController.make[IO](mockService, mockUserService).getNewsById,
          )
          .backend()
      "return news with existing Id" in {
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
      val mockService = mock[NewsService[IO]]
      val mockUserService = mock[UserService[IO]]

      val backendStub: SttpBackend[IO, Any] =
        TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
          .whenServerEndpointRunLogic(
            NewsController.make[IO](mockService, mockUserService).deleteNews,
          )
          .backend()
      "delete news with existing Id" in {
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
      val mockService = mock[NewsService[IO]]
      val mockUserService = mock[UserService[IO]]

      val backendStub: SttpBackend[IO, Any] =
        TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
          .whenServerEndpointRunLogic(
            NewsController.make[IO](mockService, mockUserService).allNews,
          )
          .backend()

      "return all existing news" in {
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
      val mockService = mock[NewsService[IO]]
      val mockUserService = mock[UserService[IO]]

      val backendStub: SttpBackend[IO, Any] =
        TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
          .whenServerEndpointRunLogic(
            NewsController.make[IO](mockService, mockUserService).getByKeyWordFromDB,
          )
          .backend()

      "return news with key word from db" in {
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
      val mockService = mock[NewsService[IO]]
      val mockUserService = mock[UserService[IO]]

      val backendStub: SttpBackend[IO, Any] =
        TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
          .whenServerEndpointRunLogic(
            NewsController.make[IO](mockService, mockUserService).getNewsByPublishedRange,
          )
          .backend()

      val start = LocalDate.parse("2023-11-10")
      val end = LocalDate.parse("2023-12-12")
      val zStart = start.atStartOfDay(ZoneId.systemDefault())
      val zEnd = end.atStartOfDay(ZoneId.systemDefault())

      "return news by date range from db" in {
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

      val username = "Admin"
      val password = "test"
      val mockService = mock[NewsService[IO]]
      val mockUserService = mock[UserService[IO]]
      when(mockUserService.isExist(UsernamePassword(username, Some(password))))
        .thenReturn(IO(true))

      val backendStub: SttpBackend[IO, Any] =
        TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
          .whenServerEndpointRunLogic(
            NewsController.make[IO](mockService, mockUserService).getNewsByKeyWord,
          )
          .backend()

      val credentials = s"$username:$password"
      val base64Credentials =
        java.util.Base64.getEncoder.encodeToString(credentials.getBytes("UTF-8"))
      val authorizationHeader = s"Basic $base64Credentials"
      val keyWord = "Insider"

      "return news with key word" in {
        when(mockService.getByKeyWord(keyWord))
          .thenReturn(IO(Some(okAPIExample)))

        val response = basicRequest
          .headers(Map("Authorization" -> authorizationHeader))
          .get(uri"http://localhost:8080/api/v1/news-api/keyWord/$keyWord")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe Ok)
          _ <- response.asserting(_.body.isRight shouldBe true)
          _ <- response.asserting(_.body shouldBe Right(okAPIExample.asJson))
        } yield ()
      }

      "return response with no results" in {
        when(mockService.getByKeyWord(keyWord))
          .thenReturn(IO(Some(notFoundAPIExample)))

        val response = basicRequest
          .headers(Map("Authorization" -> authorizationHeader))
          .get(uri"http://localhost:8080/api/v1/news-api/keyWord/$keyWord")
          .send(backendStub)

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
        when(mockService.getByKeyWord(keyWord))
          .thenReturn(IO(None))

        val response = basicRequest
          .headers(Map("Authorization" -> authorizationHeader))
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

      "fail authentication" in {
        when(mockUserService.isExist(UsernamePassword(username, Some(password))))
          .thenReturn(IO(false))

        val response = basicRequest
          .headers(Map("Authorization" -> authorizationHeader))
          .get(uri"http://localhost:8080/api/v1/news-api/keyWord/$keyWord")
          .send(backendStub)

        for {
          _ <- response.asserting(_.code shouldBe Unauthorized)
          _ <- response.asserting(_.body.isLeft shouldBe true)
          _ <- response.asserting(
            _.body shouldBe Left(
              Right(
                AuthorizationFail(s"Проблема авторизации"),
              ).asJson,
            ),
          )
        } yield ()

      }
    }

    "getHeadlinesByCategory" - {
      val mockService = mock[NewsService[IO]]
      val mockUserService = mock[UserService[IO]]

      val backendStub: SttpBackend[IO, Any] =
        TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
          .whenServerEndpointRunLogic(
            NewsController.make[IO](mockService, mockUserService).getHeadlinesByCategory,
          )
          .backend()

      "return news by category" in {
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
      val mockService = mock[NewsService[IO]]
      val mockUserService = mock[UserService[IO]]

      val backendStub: SttpBackend[IO, Any] =
        TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
          .whenServerEndpointRunLogic(
            NewsController.make[IO](mockService, mockUserService).getHeadlinesByCountry,
          )
          .backend()

      "return news by country" in {
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
