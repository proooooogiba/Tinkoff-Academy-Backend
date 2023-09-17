import scala.reflect.runtime.universe._

class Economy
class UpgradedEconomy extends Economy
class Special1b extends UpgradedEconomy
class ExtendedEconomy extends Economy
class Business extends ExtendedEconomy
class Elite extends Business
class Platinum extends Business

class ServiceLevelAdvance[-T <: Economy] {
  def advance[R <: T]: ServiceLevelAdvance[R] = new ServiceLevelAdvance[R]
}

object two extends App {
  val level1 = new ServiceLevelAdvance[Economy]
  level1.advance[Business]
  level1.advance[ExtendedEconomy]
  level1.advance[Elite]
  val level4 = new ServiceLevelAdvance[Elite]
  level4.advance[Elite]
//  level3.advance[Economy] // compile error
}
