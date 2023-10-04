package utils

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

// Don't change this file
class UtilsUpstreamSpec extends AnyFlatSpec with Matchers {
  "RepeatSyntax" should "repeat 1 times should not change" in {
    Option("Hello").times(1) shouldBe Some("Hello")
  }

  it should "repeat 2 times for Hello" in {
    Option("Hello").times(2) shouldBe Some("HelloHello")
  }

  it should "add 3 times for Int" in {
    List(3, 1).times(3) shouldBe List(9, 3)
  }

  "const" should "return const value" in {
    const(42)(Option("Hello")) shouldBe Option(42)
  }

  "fst" should "return first element from tuple" in {
    fst[Option, Int](Option((4, 2))) shouldBe Option(4)
  }

  "snd" should "return second element from tuple" in {
    snd[Option, Int](Option((4, 2))) shouldBe Option(2)
  }

  "tupled" should "return pair" in {
    tupled[Option, Int, Int](Option(4), _ + 2) shouldBe Option((4, 6))
  }

  "times" should "repeat element n times" in {
    times(Option(List(5)), 5) shouldBe Option(List(5, 5, 5, 5, 5))
  }
}
