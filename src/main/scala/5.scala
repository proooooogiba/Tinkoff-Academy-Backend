trait RepeatList[+T] extends Iterable[T]

case class Salary(employee: String, amount: Double)

class RepeatListFromIterable[+T](iterable: Iterable[T]) extends RepeatList[T] {
  // should repeat iterable indefinitely
  override def iterator: Iterator[T] = new Iterator[T] {
    private var iter = iterable.iterator
    override def hasNext: Boolean = true

    override def next(): T = {
      if (iter.isEmpty) {
        iter = iterable.iterator
      }
      iter.next()
    }
  }
}

object RepeatList {
  def apply[T](iterable: Iterable[T]): RepeatList[T] =
    new RepeatListFromIterable[T](iterable)
}

trait Multiply[M] {
  def twice(m: M): M
  def thrice(m: M): M
  def fourTimes(m: M): M
}

object MultiplyInstances {

  implicit val multiplySalary: Multiply[Salary] = new Multiply[Salary] {
    override def twice(m: Salary): Salary = Salary(m.employee, m.amount * 2)
    override def thrice(m: Salary): Salary = Salary(m.employee, m.amount * 3)
    override def fourTimes(m: Salary): Salary = Salary(m.employee, m.amount * 4)
  }

  implicit val multiplyRepeatList: Multiply[RepeatList[Int]] =
    new Multiply[RepeatList[Int]] {
      override def twice(m: RepeatList[Int]): RepeatList[Int] = RepeatList(
        m.view.flatMap(x => Iterable(x, x))
      )
      override def thrice(m: RepeatList[Int]): RepeatList[Int] = RepeatList(
        m.view.flatMap(x => Iterable(x, x, x))
      )
      override def fourTimes(m: RepeatList[Int]): RepeatList[Int] = RepeatList(
        m.view.flatMap(x => Iterable(x, x, x, x))
      )
    }
}

object MultiplySyntax {
  implicit class MultiplyOps[A](value: A) {
    def twice(implicit MultiplyInstance: Multiply[A]): A =
      MultiplyInstance.twice(value)
    def thrice(implicit MultiplyInstance: Multiply[A]): A =
      MultiplyInstance.thrice(value)

    def fourTimes(implicit MultiplyInstance: Multiply[A]): A =
      MultiplyInstance.fourTimes(value)
  }

}

import MultiplyInstances._
import MultiplySyntax.MultiplyOps

object five extends App {
  val list = RepeatList(Seq(1, 2, 3))
  val salary = Salary("Bob", 300.0)

  println(list.thrice.take(10))
  println(salary.fourTimes)
}
