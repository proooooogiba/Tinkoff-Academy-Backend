package ru.tinkoff.tictactoe.service

import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks.forAll
import org.scalatest.prop.TableFor2
import org.scalatest.prop.Tables._
import ru.tinkoff.tictactoe.model.Cells.{Coordinate, State}
import ru.tinkoff.tictactoe.model._

class GameStateCheckerSpec extends AnyFlatSpec with Matchers with EitherValues with MockFactory {
  val coordinates = List(
    Coordinate(0, 0),
    Coordinate(0, 1),
    Coordinate(0, 2),
    Coordinate(1, 0),
    Coordinate(1, 1),
    Coordinate(1, 2),
    Coordinate(2, 0),
    Coordinate(2, 1),
    Coordinate(2, 2),
  )

  val testCases: TableFor2[List[Option[Player]], Result] = Table(
    ("listOfBoard", "expectedResult"),
    (List(None, None, None, None, None, None, None, None, None), Continue),
    (List(Some(X), Some(X), Some(X), None, None, None, None, None, None), Winner(X)),
    (List(Some(O), None, None, Some(O), None, None, Some(O), None, None), Winner(O)),
    (List(Some(O), Some(X), Some(O), Some(X), Some(X), Some(O), Some(O), Some(O), Some(X)), Draw),
    (List(Some(X), None, None, None, Some(X), None, None, None, Some(X)), Winner(X)),
  )

  "gameState" should "return the correct string for the given input" in {
    forAll(testCases) { (listOfBoard, expectedResult) =>
      val states = listOfBoard.map(player => State(player))
      val boardState = coordinates.zip(states).toMap
      val calculatedResult = GameStateChecker.getGameState(boardState, 3)
      calculatedResult shouldBe expectedResult
    }
  }
}
