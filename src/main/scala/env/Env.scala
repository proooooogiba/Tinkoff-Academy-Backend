package env
// Разрешается менять данный файл как угодно

trait Env[F[_]]

object Env {
  implicit final class EnvMapSyntax(val self: Map[String, String]) extends AnyVal {
    def toEnv[F[_]]: Env[F] = ???
  }

  def stub[F[_]] : Env[F] = ???
}
