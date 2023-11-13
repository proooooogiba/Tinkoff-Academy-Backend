package ru.tinkoff.tictactoe.model

final case class Size private (val value: Int) {}

object Size {
  private final val MinValue = 3
  def fromInt(value: Int): Option[Size] =
    if (value >= MinValue) Some(new Size(value)) else None
}
