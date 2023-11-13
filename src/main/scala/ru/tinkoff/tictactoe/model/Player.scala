package ru.tinkoff.tictactoe.model

trait Player {
  def nextPlayer(): Player
}

object X extends Player {
  override def nextPlayer(): Player = O
  override def toString: String = "X"
}
object O extends Player {
  override def nextPlayer(): Player = X
  override def toString: String = "O"
}
