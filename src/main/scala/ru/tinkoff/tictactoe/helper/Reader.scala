package ru.tinkoff.tictactoe.helper

import cats.effect.IO
import retry._

import scala.concurrent.duration.DurationInt

class Reader(size: Int) {

  def readInt(name: String): IO[Int] = {
    val parser = new Parser(size)
    retryingOnSomeErrors(isWorthRetrying = isIOException, policy = policy, onError = onError) {
      for {
        _ <- IO(println(s"Enter $name number:"))
        input <- IO(scala.io.StdIn.readLine())
        num <- parser.parseInt(input)
        checkedNumber <- parser.checkNumberValue(num)
      } yield checkedNumber
    }
  }

  private val policy: RetryPolicy[IO] = RetryPolicies.constantDelay[IO](1.second)

  private def isIOException(e: Throwable): IO[Boolean] = e match {
    case _: NumberFormatException => IO.pure(true)
    case _: RuntimeException      => IO.pure(true)
    case _                        => IO.pure(false)
  }

  private def onError(err: Throwable, details: RetryDetails): IO[Unit] =
    IO(println(s"${err.getMessage}"))
}
