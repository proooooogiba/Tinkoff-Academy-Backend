import scala.reflect.runtime.universe._

class KnowNothing
class Aggressive extends KnowNothing
class KnowSomething extends KnowNothing
class PoorlyEducated extends KnowSomething
class Normal extends PoorlyEducated
class Enlightened extends Normal
class Genius extends Enlightened


class SchoolClass[T <: KnowNothing](collection: Seq[T]) {
  def accept[R >: T <: KnowNothing](students: Seq[R]): SchoolClass[R] = {
    new SchoolClass[R](collection ++ students)
  }
}


object three extends App {
  val knowNothing = new KnowNothing
  val aggressive = new Aggressive
  val knowSomething = new KnowSomething
  val poorlyEducated = new PoorlyEducated
  val normal = new Normal
  val enlightened = new Enlightened
  val genius = new Genius

  val class1 = new SchoolClass[Genius](Seq(genius))
  val class2 = class1.accept(Seq(enlightened))
  val class3 = class2.accept(Seq(normal))
  val class4 = class3.accept(Seq(poorlyEducated))
  val class5 = class4.accept(Seq(knowSomething))
  val class6 = class5.accept(Seq(aggressive))
  val class7 = class6.accept(Seq(knowNothing))

}
