import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.language.postfixOps

class GameOfThronesSpec extends AnyFlatSpec with Matchers {
  "Lannisterns money amount" should "increased in 1.7 times" in {
    val lannisters = Lannisters(wealth = Wealth(35.0, 10.0))
    val newLannistersMoney = lannisters.borrowMoney.moneyAmount

    newLannistersMoney shouldBe 59.5
  }

  "Targaryen army forces" should "increased in 2 times" in {
    val targaryen = Targaryen(wealth = Wealth(18.0, 10.0))
    val newTargaryenArmyForce = targaryen.callDragon.armyForces

    newTargaryenArmyForce shouldBe 20.0
  }

  "Game Of Thrones" should "changed " in {
    val got = new GameOfThrones(
      Targaryen(wealth = Wealth(35.0, 10.0)),
      Lannisters(wealth = Wealth(18.0, 10.0))
    )

    got.nextTurn(got.targaryen.makeWildFire)(got.lannisters.borrowMoney)
  }
}

