import cats.Functor

package object env {
  def getEnvs[F[_]: Functor: Env](envName: F[String]): F[Option[String]] = ???
}
