trait Converter[S] {
  def convert(value: S): String
}

trait Slide[R] {
  def read: (Option[R], Slide[R])
}

// OOP15-UE: slide projector
class Projector[R](converter: Converter[R]){
  def project(screen: Slide[R]): String = ???
}

class WordLine(val word: String)
// class RedactedWordLine(val redactionFactor: Double, word: String) extends ???

object LineConverter extends Converter[WordLine] {
  override def convert(value: WordLine): String = value.word + "\n"
}

class HelloSlide[R <: WordLine](lines: Seq[R]) extends Slide[R] {
  override def read: (Option[R], Slide[R]) = ???
}

object `4` extends App {
  // val slide1: HelloSlide[RedactedWordLine] = ...
}
