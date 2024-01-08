final case class Targaryen(name: String = "Таргариены", wealth: Wealth)
    extends GreatHouse
    with MakeWildFire
    with CallDragon

final case class Lannisters(name: String = "Ланистеры", wealth: Wealth)
    extends GreatHouse
    with MakeWildFire
    with BorrowMoney

case class WealthImpl(moneyAmount: Double, armyForces: Double) extends Wealth


object solution extends App {

  private val got = new GameOfThrones(
    Targaryen(wealth = Wealth(35.0, 10.0)),
    Lannisters(wealth = Wealth(18.0, 10.0))
  )

  val got2 = got.nextTurn(got.targaryen.callDragon)(got.lannisters.borrowMoney)
  val got3 =
    got2.nextTurn(got2.targaryen.makeWildFire)(got2.lannisters.borrowMoney)
  println(
    got3.lannisters.wealth.toString,
    got3.targaryen.wealth.toString
  )
}
