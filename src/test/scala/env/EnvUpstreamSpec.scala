package env

import cats.Id
import env.Env.EnvMapSyntax
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

// Don't change this file
class EnvUpstreamSpec extends AnyFlatSpec with Matchers with MockFactory {
  "Env" should "have syntax for Map" in {
    implicit val env: Env[Id] = Map.empty[String, String].toEnv

    getEnvs(Id("HTTP_PORT")) shouldBe Id(None)
  }

  it should "have stub" in {
    implicit val env: Env[Id] = Env.stub

    getEnvs(Id("HTTP_PORT")) shouldBe Id(Some("scala"))
  }

  "Env Map" should "return value from Map" in {
    implicit val env: Env[Id] = Map[String, String](
      "HTTP_PORT"                                                   -> "8080",
      "The Ultimate Question of Life, the Universe, and Everything" -> "42"
    ).toEnv

    getEnvs(Id("HTTP_PORT")) shouldBe Id(Some("8080"))
  }
}
