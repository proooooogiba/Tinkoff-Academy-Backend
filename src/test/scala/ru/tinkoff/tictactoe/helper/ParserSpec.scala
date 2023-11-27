package ru.tinkoff.tictactoe.helper

import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.EitherValues
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

class ParserSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with EitherValues {

  "parseInt" - {
    "return input number" in {
      val input = "42"
      val expected = 42

      val parser = new Parser(3)
      val result = parser.parseInt(input)
      result.asserting(_ shouldEqual expected)
    }

    "handler error" in {
      val input = "ss"
      val parser = new Parser(3)
      val result = parser
        .parseInt(input)

      result
        .assertThrowsWithMessage[NumberFormatException]("Invalid number, when parsing Int")
    }

    "handle errors with IO monad" in {
      val input = "ss"
      val parser = new Parser(3)
      val result = parser.parseInt(input).attempt

      result.asserting(_.isLeft shouldEqual true)
    }
  }

  "checkNumberValue" - {
    "return input number" in {
      val input = 2
      val expected = 2

      val parser = new Parser(3)
      val result = parser.checkNumberValue(input)
      result.asserting(_ shouldEqual expected)
    }

    "handle big number error" in {
      val input = 3
      val size = 3
      val parser = new Parser(size)

      val result = parser.checkNumberValue(input)

      result
        .assertThrowsWithMessage[RuntimeException](s"Number should be less $size")
    }

    "handle negative number error" in {
      val input = -1
      val size = 3
      val parser = new Parser(size)

      val result = parser.checkNumberValue(input)

      result
        .assertThrowsWithMessage[RuntimeException](s"Number should not be negative")
    }
  }
}
