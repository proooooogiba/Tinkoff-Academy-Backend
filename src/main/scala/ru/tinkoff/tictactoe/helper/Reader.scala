package ru.tinkoff.tictactoe.helper

import cats.effect.IO

class Reader(size: Int) {
  def readInt(name: String): IO[Int] = {
    val parser = new Parser(size)
    for {
      input <- IO(println(s"Enter $name number:")) *> IO(scala.io.StdIn.readLine())
      num <- parser.parseInt(input).handleErrorWith(err => IO(println(err)) *> readInt(name))
      checkedNumber <- parser
        .checkNumberLessThanSize(num)
        .handleErrorWith(err => IO(println(err)) *> readInt(name))
    } yield checkedNumber
  }
}
