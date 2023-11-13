package ru.tinkoff.tictactoe.model

object Cells {
  case class Coordinate(row: Int, col: Int)
  case class State(player: Option[Player])
}
