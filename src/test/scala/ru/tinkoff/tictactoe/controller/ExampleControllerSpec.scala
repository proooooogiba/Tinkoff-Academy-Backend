package ru.tinkoff.tictactoe.controller

import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3.testing.SttpBackendStub
import sttp.client3.{UriContext, basicRequest}
import sttp.tapir.server.stub.TapirStubInterpreter
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import sttp.tapir.integ.cats.effect.CatsMonadError

class ExampleControllerSpec extends AnyFlatSpec with Matchers with EitherValues {
  it should "return hello message" in {
    val backendStub = TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
      .whenServerEndpointRunLogic(ExampleController.make[IO].hello)
      .backend()

    val response = basicRequest
      .get(uri"http://test.com/api/v1/hello?name=Ilya")
      .send(backendStub)

    response.map(_.body.value shouldBe "Hello, Ilya!").unsafeRunSync()
  }
}
