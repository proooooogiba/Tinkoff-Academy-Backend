package ru.tinkoff.tictactoe.helper

import cats.effect.IO

class Parser(size: Int) {
  def parseInt(s: String): IO[Int] = IO
    .delay(s.toInt)
    .handleErrorWith(_ =>
      IO.raiseError(new NumberFormatException("Invalid number, when parsing Int")),
    )

  def checkNumberValue(num: Int): IO[Int] =
    if (num >= size) IO.raiseError(new RuntimeException(s"Number should be less $size"))
    else if (num < 0) IO.raiseError(new RuntimeException(s"Number should not be negative"))
    else IO(num)
}
