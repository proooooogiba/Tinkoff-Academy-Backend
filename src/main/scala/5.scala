trait RepeatList[+T] extends Iterable[T]
case class Salary(employee: String, amount: Double)

class RepeatListFromIterable[+T](iterable: Iterable[T]) extends RepeatList[T] {
  // should repeat iterable indefinitely
  override def iterator: Iterator[T] = ???
}

object RepeatList {
  def apply[T](iterable: Iterable[T]): RepeatList[T] = new RepeatListFromIterable[T](iterable)
}

trait Multiply[M] {
  def twice(m: M): M
  def thrice(m: M): M
  def fourTimes(m: M): M
}

object `5` extends App {
  val list = RepeatList(Seq(1,2,3))
  val salary = Salary("Bob", 300.0)

//  println(list.thrice.take(10))

//  println(salary.fourTimes)

}
