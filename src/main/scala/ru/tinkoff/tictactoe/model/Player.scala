package ru.tinkoff.tictactoe.model

trait Player {
  def nextPlayer(): Player
}
