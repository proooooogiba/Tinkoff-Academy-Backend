package ru.tinkoff.tictactoe.service

import ru.tinkoff.tictactoe.model.Cells.{Coordinate, State}
import ru.tinkoff.tictactoe.model._

object GameStateChecker {
  def getGameState(gameMap: Map[Coordinate, State], size: Int): Result =
    checkWinner(gameMap, size) match {
      case None         => if (checkDraw(gameMap)) Draw else Continue
      case Some(player) => Winner(player)
    }

  private def checkDraw(gameMap: Map[Coordinate, State]): Boolean =
    !gameMap.values.exists(_.player.isEmpty)

  private def checkWinner(gameMap: Map[Coordinate, State], size: Int): Option[Player] = {
    def checkRowWin(row: Int): Option[Player] = {
      val states = getStatesInRow(row)
      if (hasWonCombination(states)) {
        states.head.player
      } else {
        None
      }
    }

    def hasWonCombination(states: Seq[State]) =
      states.forall(_.player.isDefined) && states.map(_.player.get).toSet.size == 1

    def checkColWin(col: Int): Option[Player] = {
      val states = getStatesInCol(col)
      if (hasWonCombination(states)) {
        states.head.player
      } else {
        None
      }
    }

    def getStatesInRow(row: Int): Seq[State] =
      (0 until size).map(col => gameMap.getOrElse(Coordinate(row, col), State(None)))

    def getStatesInCol(col: Int): Seq[State] =
      (0 until size).map(row => gameMap.getOrElse(Coordinate(row, col), State(None)))

    def checkDiagonalWin(): Option[Player] = {
      val mainDiagonalStates =
        (0 until size).map(i => gameMap.getOrElse(Coordinate(i, i), State(None)))
      val antiDiagonalStates =
        (0 until size).map(i => gameMap.getOrElse(Coordinate(i, size - 1 - i), State(None)))

      def checkDiagonal(states: Seq[State]): Option[Player] =
        if (hasWonCombination(states)) {
          states.head.player
        } else {
          None
        }

      checkDiagonal(mainDiagonalStates).orElse(checkDiagonal(antiDiagonalStates))
    }

    (0 until size)
      .flatMap(i => List(checkRowWin(i), checkColWin(i)))
      .find(_.isDefined)
      .flatten
      .orElse(checkDiagonalWin())
  }
}
