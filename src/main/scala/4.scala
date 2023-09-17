import scala.util.Random

trait Converter[-S <: WordLine] {
  def convert(value: S): String
}

trait Slide[+R <: WordLine] {
  def read: (Option[R], Slide[R])
}

// OOP15-UE: slide projector
class Projector[R <: WordLine](converter: Converter[R]) {
  def project(screen: Slide[R]): String = ???
}

class WordLine(val word: String)

class RedactedWordLine(val redactionFactor: Double, override val word: String)
    extends WordLine(word)

object LineConverter extends Converter[WordLine] {
  override def convert(value: WordLine): String = value.word + "\n"
}

class HelloSlide[R <: WordLine](lines: Seq[R]) extends Slide[R] {
  override def read: (Option[R], Slide[R]) =
    (lines.headOption, new HelloSlide[R](lines.drop(1)))
}

object RedactedWordLineConverter extends Converter[RedactedWordLine] {
  override def convert(value: RedactedWordLine): String = if (
    Random.nextDouble() < value.redactionFactor
  ) value.word.replaceAll(".*", "█")
  else value.word
}

object four extends App {
  val wordLineSlideContent = List(
    new WordLine("Hello"),
    new WordLine("there!"),
    new WordLine("I am using Scala 2!")
  )

  val redactedWordLineSlideContent = List(
    new RedactedWordLine(0.3, "Hello"),
    new RedactedWordLine(0.1, "there!"),
    new RedactedWordLine(0.4, "I am using Scala 2!")
  )

  val redactedWordLineSlide =
    new HelloSlide[RedactedWordLine](redactedWordLineSlideContent)
  val wordLineSlide = new HelloSlide[WordLine](wordLineSlideContent)

//  3. В проекторе для RedactedWordLine можно проецировать Slide[RedactedWordLine], но нельзя Slide[WordLine]
  val projectorRedactedWordLine =
    new Projector[RedactedWordLine](RedactedWordLineConverter)
  projectorRedactedWordLine.project(redactedWordLineSlide)
//  projectorRedactedWordLine.project(wordLineSlide) // type is mismatch

//  4. В проекторе для WordLine можно проецировать Slide[WordLine] и Slide[RedactedWordLine]
  val projectorWordLine = new Projector[WordLine](LineConverter)
  projectorWordLine.project(wordLineSlide)
  projectorWordLine.project(redactedWordLineSlide)

//  5. В проекторе для RedactedWordLine можно использовать Converter[RedactedWordLine] и Converter[WordLine]
  val projectorRedactedWordLine2 =
    new Projector[RedactedWordLine](RedactedWordLineConverter)
  val projectorRedactedWordLine3 =
    new Projector[RedactedWordLine](LineConverter)

//  6. В проекторе для WordLine можно использовать Converter[WordLine], но нельзя Converter[RedactedWordLine]
  val projectorWordLine2 = new Projector[WordLine](LineConverter)
//  val projectorWordLine3 = new Projector[WordLine](RedactedWordLineConverter) // type is mismatch
}
