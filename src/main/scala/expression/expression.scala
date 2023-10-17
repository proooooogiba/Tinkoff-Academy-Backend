import Expression._
import cats.Monad
import cats.syntax.flatMap._
import cats.syntax.functor._

import scala.annotation.tailrec // for flatMap

object Expression {
  sealed trait Expression[T]
  final case class Plus[T](a: Expression[T], b: Expression[T]) extends Expression[T]

  final case class Diff[T](a: Expression[T], b: Expression[T]) extends Expression[T]

  final case class Mul[T](a: Expression[T], b: Expression[T]) extends Expression[T]

  final case class Div[T](a: Expression[T], b: Expression[T]) extends Expression[T]

  final case class Const[T](a: T) extends Expression[T]

  def plus[A](left: Expression[A], right: Expression[A]): Expression[A] = Plus(left, right)

  def diff[A](left: Expression[A], right: Expression[A]): Expression[A] = Diff(left, right)

  def mul[A](left: Expression[A], right: Expression[A]): Expression[A] = Mul(left, right)

  def div[A](left: Expression[A], right: Expression[A]): Expression[A] = Div(left, right)

  def const[A](value: A): Const[A] = Const(value)
}

object ExpressionInstances {
  implicit val expressionMonad = new Monad[Expression] {
    override def flatMap[A, B](fa: Expression[A])(f: A => Expression[B]): Expression[B] = {
      fa match {
        case Plus(a, b) => Plus(flatMap(a)(f), flatMap(b)(f))
        case Diff(a, b) => Diff(flatMap(a)(f), flatMap(b)(f))
        case Mul(a, b)  => Mul(flatMap(a)(f), flatMap(b)(f))
        case Div(a, b)  => Div(flatMap(a)(f), flatMap(b)(f))
        case Const(x)   => f(x)
      }
    }

    override def tailRecM[A, B](a: A)(f: A => Expression[Either[A, B]]): Expression[B] = {
      @tailrec
      def loop(
          open: List[Expression[Either[A, B]]],
          closed: List[Option[Expression[B]]],
          op: List[Option[String]]
      ): List[Expression[B]] =
        open match {
          case Plus(left, right) :: next =>
            loop(left :: right :: next, None :: closed, Some("+") :: op)

          case Diff(left, right) :: next =>
            loop(left :: right :: next, None :: closed, Some("-") :: op)

          case Mul(left, right) :: next =>
            loop(left :: right :: next, None :: closed, Some("*") :: op)

          case Div(left, right) :: next =>
            loop(left :: right :: next, None :: closed, Some("/") :: op)

          case Const(Left(x)) :: next =>
            loop(f(x) :: next, closed, op)
          case Const(Right(x)) :: next =>
            loop(next, Some(pure(x)) :: closed, None :: op)

          case Nil =>
            closed.zip(op).foldLeft(Nil: List[Expression[B]]) { (acc, tupleExpressionOp) =>
              val (maybeExpression, op) = tupleExpressionOp
              maybeExpression.map(_ :: acc).getOrElse {
                val left :: right :: tail = acc
                op match {
                  case Some("+") => plus(left, right) :: tail
                  case Some("-") => diff(left, right) :: tail
                  case Some("*") => mul(left, right) :: tail
                  case Some("/") => div(left, right) :: tail
                  case _         => throw new IllegalArgumentException("Unsupported expression")
                }
              }
            }
        }

      loop(List(f(a)), Nil, Nil).head
    }

    override def pure[A](x: A): Expression[A] = Const(x)
  }
}

import ExpressionInstances.expressionMonad
object Homework extends App {
  val expr1: Expression[Double] = Plus(Const(5), Plus(Const(10), Const(-1)))
  val expr2: Expression[Int]    = Plus(Const(5), Const(6))

  println(expr1.map(x => 1.8 * x))
  println(expr2.flatMap(x => Diff(Const(x - 1), Const(x + 1))))

  val expr3: Expression[Int] = for {
    a <- plus(const(1), const(2))
    b <- diff(const(a * 10), const(a))
    c <- mul(const(b), const(b + 1))
  } yield c

  println(expr3)

  val m: Expression[Int] =
    Plus(Const(3), Div(Mul(Const(12), Diff(Const(7), Const(4))), Const(2)))
  def func[T]: Expression[T] => Expression[Any] = (x: Expression[T]) =>
    diff(const(x), plus(const(x), const(1)))

  def f[T]: T => Expression[Any] = (x: T) => plus(Const(x), Const(1))

  def g[T]: T => Expression[Any] = (x: T) => mul(Const(x), Const(x))

  // Monad Laws
  println(expressionMonad.pure(m).flatMap(func) == func(m))           // true
  println(m.flatMap(expressionMonad.pure) == m)                       // true
  println(m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))) // true

}
