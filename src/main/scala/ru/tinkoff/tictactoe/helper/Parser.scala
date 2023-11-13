package ru.tinkoff.tictactoe.helper

import cats.effect.IO

class Parser(size: Int) {
  def readInt(name: String): IO[Int] = for {
    input <- IO(println(s"Enter $name number:")) *> IO(scala.io.StdIn.readLine())
    num <- parseInt(input).handleErrorWith(err => IO(println(err)) *> readInt(name))
    checkedNumber <- checkNumberLessThanSize(num).handleErrorWith(err =>
      IO(println(err)) *> readInt(name),
    )
  } yield checkedNumber

  def parseInt(s: String): IO[Int] = IO
    .delay(s.toInt)
    .handleErrorWith(_ =>
      IO.raiseError(new NumberFormatException("Invalid number, when parsing Int")),
    )

  def checkNumberLessThanSize(num: Int): IO[Int] =
    if (num >= size) IO.raiseError(new RuntimeException(s"Number should be less $size"))
    else IO(num)
}
