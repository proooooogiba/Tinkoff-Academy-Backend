package env

import cats.Id

import scala.util.Try

// Разрешается менять данный файл как угодно
trait Env[F[_]] {
  def getEnv(envName: String): Option[String]
}

object Env {
  implicit class EnvMapSyntax(val self: Map[String, String]) extends AnyVal {
    def toEnv[F[_]]: Env[F] = (envName: String) => self.get(envName)
  }

  def stub[F[_]]: Env[F] = (_: String) => Some("scala")

  def system[F[_]]: Env[F] = (envName: String) => Try(sys.env(envName)).toOption
}

object main extends App {
  implicit val env: Env[Id] = Env.system

  println(getEnvs(Id("USERNAME"))) // Your username

  println(getEnvs(Id("USERNAME1"))) // None
}
