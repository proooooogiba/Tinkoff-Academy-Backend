package ru.tinkoff.tictactoe.model

import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SizeSpec extends AnyFlatSpec with Matchers with EitherValues {

  "fromInt" should "return input Some(Size)" in {
    val input = 12
    Size.fromInt(input) shouldBe Some(Size(input))
  }

  it should "return None with invalid input" in {
    val invalidInput = 2
    Size.fromInt(invalidInput) shouldBe None
  }
}
