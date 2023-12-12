package ru.tinkoff.newsaggregator.controller.news

import sttp.model.StatusCode
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.{EndpointOutput, oneOfVariantValueMatcher}
import tethys.JsonObjectWriter.lowPriorityWriter
import tethys.derivation.auto.{jsonReaderMaterializer, jsonWriterMaterializer}

object ControllerErrors {
  case class ServerError(what: String)
  sealed trait UserError
  case class UserBadRequest(what: String) extends UserError
  case class ResourceNotFound(what: String) extends UserError
  case class AuthorizationFail(what: String) extends UserError

  val notFoundUserError: EndpointOutput.OneOfVariant[Right[ServerError, ResourceNotFound]] =
    oneOfVariantValueMatcher(
      StatusCode.NotFound,
      jsonBody[Right[ServerError, ResourceNotFound]]
        .description("Новость не найдена")
        .example(Right(ResourceNotFound("Новости по ключевому слову не были найдены"))),
    ) { case Right(ResourceNotFound(_)) =>
      true
    }

  val authorizationFail: EndpointOutput.OneOfVariant[Right[ServerError, AuthorizationFail]] =
    oneOfVariantValueMatcher(
      StatusCode.NotFound,
      jsonBody[Right[ServerError, AuthorizationFail]]
        .description("Проблема аутентификации")
        .example(Right(AuthorizationFail("Неверное имя пользователя или пароль"))),
    ) { case Right(AuthorizationFail(_)) =>
      true
    }

  val notFoundByRangeUserError: EndpointOutput.OneOfVariant[Right[ServerError, ResourceNotFound]] =
    oneOfVariantValueMatcher(
      StatusCode.NotFound,
      jsonBody[Right[ServerError, ResourceNotFound]]
        .description("Новость не найдена")
        .example(Right(ResourceNotFound("Новости в данном диапазоне не были найдены"))),
    ) { case Right(ResourceNotFound(_)) =>
      true
    }

  val badRequestByRangeUserError: EndpointOutput.OneOfVariant[Right[ServerError, UserBadRequest]] =
    oneOfVariantValueMatcher(
      StatusCode.BadRequest,
      jsonBody[Right[ServerError, UserBadRequest]]
        .description("Некорректный диапазон")
        .example(
          Right(
            UserBadRequest(
              "Введённый диапазон некорректен: начальная дата не может быть больше конечной",
            ),
          ),
        ),
    ) { case Right(UserBadRequest(_)) =>
      true
    }

  val internalServerError: EndpointOutput.OneOfVariant[Left[ServerError, UserError]] =
    oneOfVariantValueMatcher(
      StatusCode.InternalServerError,
      jsonBody[Left[ServerError, UserError]]
        .description("Внутренняя ошибка сервера")
        .example(
          Left(
            ServerError(
              s"Проблема отправки запроса на внешний сервис:" +
                s" убедитесь, что запрос отправляемый на News.org правильный",
            ),
          ),
        ),
    ) { case Left(ServerError(_)) =>
      true
    }

  val errorToConnectExternalService: Left[Left[ServerError, Nothing], Nothing] = Left(
    Left(
      ServerError(
        s"Проблема отправки запроса на внешний сервис:" +
          s" убедитесь, что запрос отправляемый на News.org правильный",
      ),
    ),
  )

  val errorOfExternalService: Left[Left[ServerError, Nothing], Nothing] = Left(
    Left(ServerError(s"Ошибка на стороне внешнего сервиса")),
  )

  val resourceNotFoundError: ResourceNotFound = ResourceNotFound(
    "Сохранённые новости не найдены: добавьте и они здесь появятся",
  )

  val resourceNotFoundGet: ResourceNotFound = ResourceNotFound(
    "Новость не найдена: новости с данным uuid не существует",
  )

  val resourceNotFoundDel: ResourceNotFound = ResourceNotFound(
    "Новость не удалена: новости с данным uuid не существует",
  )

  val resourceNotFoundByKeyWord: ResourceNotFound = ResourceNotFound(
    "Новость по ключевому слову не были найдены",
  )
}
