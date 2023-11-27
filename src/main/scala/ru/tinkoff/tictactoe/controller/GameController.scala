package ru.tinkoff.tictactoe.controller

import cats.effect.IO
import ru.tinkoff.tictactoe.helper.Reader
import ru.tinkoff.tictactoe.model.Cells.Coordinate
import ru.tinkoff.tictactoe.model.{Continue, Draw, Player, Result, Winner, X}
import ru.tinkoff.tictactoe.service.Game

class GameController(game: Game) {
  def runGame(): IO[Result] = {
    def iterationGame(player: Player): IO[Result] = {
      val result = for {
        coordinate <- getCoordinate
        result <- game.makeTurn(coordinate, player)
        _ <- result match {
          case Left(err) =>
            IO.println(err) >> iterationGame(player)
          case Right(_) => IO(Continue)
        }
        boardString <- game.getBoardString
        _ <- IO(println(boardString))
        res <- game.getGameState
      } yield res

      result.flatMap {
        case Continue       => iterationGame(player.nextPlayer())
        case Winner(player) => IO(Winner(player))
        case Draw           => IO(Draw)
      }
    }

    iterationGame(X)
  }

  private def getCoordinate: IO[Coordinate] = {
    val reader = new Reader(game.getSize)
    for {
      row <- reader.readInt("row")
      col <- reader.readInt("col")
    } yield Coordinate(row, col)
  }
}
