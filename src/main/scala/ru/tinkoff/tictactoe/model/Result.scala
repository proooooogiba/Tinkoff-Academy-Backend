package ru.tinkoff.tictactoe.model

sealed trait Result
case object Continue extends Result
case object Draw extends Result
case class Winner(player: Player) extends Result
