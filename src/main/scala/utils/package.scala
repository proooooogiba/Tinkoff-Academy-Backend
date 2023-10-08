import cats.implicits.{catsSyntaxFunctorTuple2Ops, catsSyntaxSemigroup, toFunctorOps}
import cats.{Functor, Semigroup}

package object utils {
  def const[F[_]: Functor, T](const: T)(other: F[_]): F[T]           = other.map(_ => const)
  def fst[F[_]: Functor, T](other: F[(T, _)]): F[T]                  = other._1F
  def snd[F[_]: Functor, T](other: F[(_, T)]): F[T]                  = other._2F
  def tupled[F[_]: Functor, A, B](other: F[A], f: A => B): F[(A, B)] = other.fproduct(f)
  def times[F[_]: Functor, A: Semigroup](other: F[A], repeat: Int): F[A] =
    other.map(x => x.combineN(repeat))
  implicit final class RepeatSyntax[F[_], A](val other: F[A]) extends AnyVal {
    def times(repeat: Int)(implicit F: Functor[F], S: Semigroup[A]): F[A] =
      utils.times(other, repeat)
  }

  implicit final class TupledSyntax[F[_], A](val other: F[A]) extends AnyVal {
    def tupled[B](f: A => B)(implicit F: Functor[F]): F[(A, B)] =
      utils.tupled(other, f)
  }

}
