import Expression._
import cats.Monad
import cats.syntax.flatMap._
import cats.syntax.functor._ // for flatMap

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

    override def tailRecM[A, B](a: A)(f: A => Expression[Either[A, B]]): Expression[B] =
      flatMap(f(a)) {
        case Left(value)  => tailRecM(value)(f)
        case Right(value) => Const(value)
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

  val expr3: Expression[Int] = for {  println(expr3)

    a <- plus(const(1), const(2))
    b <- diff(const(a * 10), const(a))
    c <- mul(const(b), const(b + 1))
  } yield c

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
