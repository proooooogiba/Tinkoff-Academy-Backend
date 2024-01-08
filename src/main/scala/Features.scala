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
