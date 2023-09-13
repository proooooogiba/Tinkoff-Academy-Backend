trait GreatHouse {
  val name: String
  val wealth: Wealth
}

trait Wealth {
  var moneyAmount: Double
  var armyForces: Double
}

trait MakeWildFire {
  this: GreatHouse =>
  def makeWildFire: Wealth = {
    wealth.armyForces *= 1.5
    wealth
  }
}

trait BorrowMoney {
  this: GreatHouse =>
  def borrowMoney: Wealth = {
    wealth.moneyAmount *= 1.7
    wealth
  }
}

trait CallDragon {
  this: GreatHouse =>
  def callDragon: Wealth = {
    wealth.armyForces *= 2
    wealth
  }
}

final case class Targaryen(name: String, wealth: Wealth)
    extends GreatHouse
    with MakeWildFire
    with CallDragon {
  def apply(name: String, targaryenMove: Wealth): Targaryen =
    Targaryen(name, targaryenMove)
}

final case class Lannisters(name: String, wealth: Wealth)
    extends GreatHouse
    with MakeWildFire
    with BorrowMoney {
  def apply(name: String, lannistersMove: Wealth): Lannisters =
    Lannisters(name, lannistersMove)
}

class GameOfThrones(targaryen: Targaryen, lannisters: Lannisters) {
  private var moveNumber = 0
  def nextTurn(targaryenMove: Wealth)(lannistersMove: Wealth): GameOfThrones = {
    moveNumber += 1
    new GameOfThrones(
      Targaryen(targaryen.name, targaryenMove),
      Lannisters(lannisters.name, lannistersMove)
    )
  }
}
object wealthLanissters extends Wealth {
  override var moneyAmount: Double = 35.0
  override var armyForces: Double = 10.0
}

object wealthTargaryen extends Wealth {
  override var moneyAmount: Double = 10.0
  override var armyForces: Double = 18.0
}

object solution extends App {
  private val tar = Targaryen("Таргариены", wealthTargaryen)
  private val lan = Lannisters("Ланистеры", wealthLanissters)
  private var gameOfThrones = new GameOfThrones(tar, lan)
  gameOfThrones = gameOfThrones
    .nextTurn(tar.callDragon)(lan.makeWildFire)
    .nextTurn(tar.callDragon)(lan.borrowMoney)
}
