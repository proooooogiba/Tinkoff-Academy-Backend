package ru.tinkoff.petstore.controller

import cats.Applicative
import ru.tinkoff.petstore.common.controller.Controller
import sttp.tapir._
import sttp.tapir.server.ServerEndpoint

class ExampleController[F[_]: Applicative] extends Controller[F] {
  val hello: ServerEndpoint[Any, F] =
    endpoint.get
      .withTag("Example")
      .summary("Сказать привет")
      .in("api" / "v1" / "hello" / query[String]("name"))
      .out(stringBody)
      .serverLogicSuccess(name => Applicative[F].pure(s"Hello, $name!"))

  override val endpoints: List[ServerEndpoint[Any, F]] =
    List(hello)
}

object ExampleController {
  def make[F[_]: Applicative]: ExampleController[F] = new ExampleController[F]
}
