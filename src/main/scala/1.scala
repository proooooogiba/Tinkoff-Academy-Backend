object Russian

object English

trait Greeting[T] {
  def text: String
}

class Greeter[T] {
  def greet(greetings: Greeting[T]): Unit = println(greetings.text)
}

class RussianGreeting(greeting: String) extends Greeting[Russian.type] {
  override def text: String = greeting
}

class EnglishGreeting(greeting: String) extends Greeting[English.type] {
  override def text: String = greeting
}

object one extends App {
  private val greeter = new Greeter[Russian.type]
  private val greeting1 = new RussianGreeting("Привет!")
  private val greeting2 = new RussianGreeting("Здравствуйте!")
  private val greeting3 = new EnglishGreeting("Hello!")

  greeter.greet(greeting1)
  greeter.greet(greeting2)

//  greeter.greet(greeting3) // Type mismatch
}
