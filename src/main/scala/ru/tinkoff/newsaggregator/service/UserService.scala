package ru.tinkoff.newsaggregator.service

import cats.Applicative
import ru.tinkoff.newsaggregator.repository.UserRepository
import sttp.tapir.model.UsernamePassword

trait UserService[F[_]] {
  def isExist(usernamePassword: UsernamePassword): Boolean
}

object UserService {
  private class Impl[F[_]: Applicative](
      userRepository: UserRepository[F],
  ) extends UserService[F] {
    override def isExist(usernamePassword: UsernamePassword): Boolean =
      usernamePassword.password match {
        case Some(password) =>
          userRepository.isExists(usernamePassword.username, password) match {
            case Some(true) => true
            case _          => false
          }
        case None => false
      }
  }

  def make[F[_]: Applicative](
      userRepository: UserRepository[F],
  ): UserService[F] =
    new Impl[F](userRepository)
}
