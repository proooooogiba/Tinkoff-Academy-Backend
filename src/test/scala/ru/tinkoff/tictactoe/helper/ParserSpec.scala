package ru.tinkoff.tictactoe.helper

import cats.effect.unsafe.implicits.global
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ParserSpec extends AnyFlatSpec with Matchers with EitherValues {

  "parseInt" should "return input number" in {
    val input = "42"
    val expected = 42

    val parser = new Parser(3)
    val result = parser.parseInt(input).unsafeRunSync()
    result shouldEqual expected
  }

  it should "handler error" in {
    val input = "ss"
    val parser = new Parser(3)
    val result = intercept[NumberFormatException] {
      parser.parseInt(input).unsafeRunSync()
    }

    result.getMessage shouldBe "Invalid number, when parsing Int"
    result.getClass shouldBe classOf[NumberFormatException]
  }

  it should "handle errors with IO monad" in {
    val input = "ss"
    val parser = new Parser(3)
    val result = parser.parseInt(input).attempt.unsafeRunSync()

    result.isLeft shouldBe true
  }

  "checkNumberLessThanSize" should "return input number" in {
    val input = 2
    val expected = 2

    val parser = new Parser(3)
    val result = parser.checkNumberLessThanSize(input).unsafeRunSync()
    result shouldEqual expected
  }

  it should "handler error" in {
    val input = 3
    val size = 3
    val parser = new Parser(size)
    val result = intercept[RuntimeException] {
      parser.checkNumberLessThanSize(input).unsafeRunSync()
    }

    result.getMessage shouldEqual s"Number should be less $size"
  }

  it should "handle errors with IO monad" in {
    val input = 3
    val size = 3
    val parser = new Parser(size)
    val result = parser.checkNumberLessThanSize(input).attempt.unsafeRunSync()

    result.isLeft shouldBe true
  }
}
