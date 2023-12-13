package ru.tinkoff.newsaggregator.service

import cats.Applicative
import cats.implicits.toFunctorOps
import ru.tinkoff.newsaggregator.repository.UserRepository
import sttp.tapir.model.UsernamePassword

trait UserService[F[_]] {
  def isExist(usernamePassword: UsernamePassword): F[Boolean]
}

object UserService {
  private class Impl[F[_]: Applicative](
      userRepository: UserRepository[F],
  ) extends UserService[F] {
    override def isExist(usernamePassword: UsernamePassword): F[Boolean] =
      usernamePassword.password match {
        case Some(password) =>
          userRepository.isExists(usernamePassword.username, password).map {
            case Some(true) => true
            case _          => false
          }
        case None => Applicative[F].pure(false)
      }
  }

  def make[F[_]: Applicative](
      userRepository: UserRepository[F],
  ): UserService[F] =
    new Impl[F](userRepository)
}
