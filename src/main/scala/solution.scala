trait GreatHouse {
  val name: String
  val wealth: Wealth
}

trait Wealth {
  val moneyAmount: Double
  val armyForces: Double
}

trait MakeWildFire {
  this: GreatHouse =>
  def makeWildFire: Wealth = Wealth(wealth.moneyAmount, wealth.armyForces * 1.5)
}

trait BorrowMoney {
  this: GreatHouse =>
  def borrowMoney: Wealth = Wealth(wealth.moneyAmount * 1.7, wealth.armyForces)
}

trait CallDragon {
  this: GreatHouse =>
  def callDragon: Wealth = Wealth(wealth.moneyAmount, wealth.armyForces * 2)
}

final case class Targaryen(name: String = "Таргариены", wealth: Wealth)
    extends GreatHouse
    with MakeWildFire
    with CallDragon

final case class Lannisters(name: String = "Ланистеры", wealth: Wealth)
    extends GreatHouse
    with MakeWildFire
    with BorrowMoney

class GameOfThrones(
    val targaryen: Targaryen,
    val lannisters: Lannisters,
    private val moveNumber: Int = 0
) {

  def nextTurn(targaryenMove: Wealth)(lannistersMove: Wealth): GameOfThrones = {
    new GameOfThrones(
      Targaryen(wealth = targaryenMove),
      Lannisters(wealth = lannistersMove),
      moveNumber + 1
    )
  }
}

case class WealthImpl(moneyAmount: Double, armyForces: Double) extends Wealth

object Wealth {
  def apply(moneyAmount: Double, armyForces: Double): Wealth =
    WealthImpl(moneyAmount, armyForces)
}

object solution extends App {

  private val got = new GameOfThrones(
    Targaryen(wealth = Wealth(35.0, 10.0)),
    Lannisters(wealth = Wealth(18.0, 10.0))
  )

  val got2 = got.nextTurn(got.targaryen.callDragon)(got.lannisters.borrowMoney)
  val got3 = got2.nextTurn(got2.targaryen.makeWildFire)(got2.lannisters.borrowMoney)
  println(got3.lannisters.wealth.toString, got3.targaryen.wealth.toString) // (WealthImpl(52.019999999999996,10.0),WealthImpl(35.0,30.0))
}
