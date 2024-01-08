trait Wealth {
  val moneyAmount: Double
  val armyForces: Double
}

object Wealth {
  def apply(moneyAmount: Double, armyForces: Double): Wealth =
    WealthImpl(moneyAmount, armyForces)
}
