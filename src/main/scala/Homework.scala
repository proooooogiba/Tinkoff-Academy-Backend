import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.annotation.tailrec

sealed trait Expression
case class Plus(a: Expression, b: Expression) extends Expression
case class Diff(a: Expression, b: Expression) extends Expression
case class Mul(a: Expression, b: Expression)  extends Expression
case class Div(a: Expression, b: Expression)  extends Expression
case class Const(v: Int) extends Expression {
  def plus(other: Const): Const = Const(v + other.v)

  def minus(other: Const): Const = Const(v - other.v)

  def multiply(other: Const): Const = Const(v * other.v)

  def div(other: Const): Const = Const(v / other.v)
}

trait Calculate[T] {
  def calculate(e: T): T
}
trait Serializable[T] {
  def serialize(e: T): String
}

trait Parseable[T] {
  def parse(e: String): T
}

trait Printable[T] {
  def print(e: T): Unit
}

object Homework extends App {
  object Serializable {
    implicit val expressionSerializble: Serializable[Expression] =
      _.toString
        .replaceAll("Plus", "+")
        .replaceAll("Diff", "-")
        .replaceAll("Div", "/")
        .replaceAll("Mul", "*")
        .replaceAll("Const\\((-?\\d+)\\)", "$1")
  }

  implicit class SerializableSyntax[T](val e: T) {
    def show(implicit v: Serializable[T]): String = v.serialize(e)
  }
  object Calculatable {
    implicit val expressionCalculatable: Calculate[Expression] = new Calculate[Expression] {
      override def calculate(e: Expression): Const = {
        e match {
          case Plus(a, b) => calculate(a).plus(calculate(b))
          case Diff(a, b) => calculate(a).minus(calculate(b))
          case Mul(a, b)  => calculate(a).multiply(calculate(b))
          case Div(a, b)  => calculate(a).div(calculate(b))
          case c: Const   => c
        }
      }
    }
  }

  implicit class CalculatableSyntax[T](val e: T) {
    def calculate(implicit v: Calculate[T]): T = implicitly[Calculate[T]].calculate(e)
  }

  object Parseable {
    def parse[T](e: String): Expression = {

      def parseIterate(result: String): String = {
        import Homework.Calculatable.expressionCalculatable
        val pattern = "(\\+|\\-|\\*|\\/)\\((-?\\d+),(-?\\d+)\\)".r
        pattern.replaceAllIn(
          result,
          m => {
            val group1 = m.group(1)
            val group2 = Const(m.group(2).toInt)
            val group3 = Const(m.group(3).toInt)

            val expr = group1 match {
              case "+" => Plus(group2, group3)
              case "-" => Diff(group2, group3)
              case "*" => Mul(group2, group3)
              case "/" => Div(group2, group3)
            }
            s"${expr.asInstanceOf[Expression].calculate}"
              .replaceAll("Const\\((-?\\d+)\\)", "$1")
          }
        )
      }

      @tailrec
      def recursiveParse(e: String): String = {
        if (e != parseIterate(e)) {
          recursiveParse(parseIterate(e))
        } else {
          e
        }
      }

      Const(recursiveParse(e).toIntOption.getOrElse(0)).asInstanceOf[Expression]
    }

    implicit val expressionParseable: Parseable[Expression] = (e: String) => {
      parse(e)
    }
  }

  object Printable {
    implicit val expressionPrintableConsole: Printable[Expression] = (e: Expression) => println(e)
    implicit def expressionPrintableFile(
        outputFile: String = "file.txt"
    ): Printable[Expression] =
      (e: Expression) => {
        import Homework.Serializable.expressionSerializble
        Files.write(Paths.get(outputFile), e.show.getBytes(StandardCharsets.UTF_8))
        ()
      }
  }

  def print[A](e: A)(implicit p: Printable[A]): Unit = p.print(e)

  def parse[A](e: String)(implicit p: Printable[A], v: Parseable[A]): Unit = p.print(v.parse(e))

  import Homework.Calculatable.expressionCalculatable
  import Homework.Parseable.expressionParseable
  import Homework.Printable.{expressionPrintableConsole, expressionPrintableFile}
  import Homework.Serializable.expressionSerializble

  val expr1: Expression = Plus(Const(3), Div(Mul(Const(12), Diff(Const(7), Const(4))), Const(2)))
  val expr2: Expression = Plus(Plus(Const(3), Const(2)), Diff(Const(5), Const(14)))
  val expr3: Expression = Plus(Const(5), Plus(Const(10), Const(-1)))

  print(expr1.calculate)(expressionPrintableFile(outputFile = "output.txt"))
  parse(expr2.show)(expressionPrintableFile(), expressionParseable)
  print(expr3)
}
