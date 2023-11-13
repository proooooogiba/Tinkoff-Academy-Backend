package ru.tinkoff.tictactoe.service

import cats.effect.{IO, Ref}
import ru.tinkoff.tictactoe.helper.Reader
import ru.tinkoff.tictactoe.model.Cells.{Coordinate, State}
import ru.tinkoff.tictactoe.model.{Player, Result}
class Game(ref: Ref[IO, Map[Coordinate, State]], size: Int) {

  private def getState(coordinate: Coordinate): IO[State] =
    ref.get.map(_.getOrElse(coordinate, State(None)))

  def makeTurn(coordinate: Coordinate, player: Player): IO[Either[String, State]] =
    for {
      state <- getState(coordinate)
      result <- state.player match {
        case Some(_) => IO(Left(s"Current cell is already engaged, enter coordinates again"))
        case None =>
          ref.modify { allPositions =>
            val mayBePosition = allPositions.get(coordinate).map { position =>
              position.copy(player = Some(player))
            }
            val newPositions = allPositions ++ mayBePosition.map(m => (coordinate, m))
            mayBePosition match {
              case Some(position) => (newPositions, Right(position))
              case None => (newPositions, Left(s"Error when getting position by coordinate"))
            }
          }
      }
    } yield result

  def printBoard: IO[Unit] = {
    def printCellState(state: State): String = state.player.map(_.toString).getOrElse("_")
    for {
      board <- ref.get
      rows = (0 until size).map { row =>
        (0 until size).map { col =>
          val coordinate = Coordinate(row, col)
          printCellState(board.getOrElse(coordinate, State(None))) + " "
        }.mkString
      }
      _ <- IO.println(rows.mkString("\n"))
    } yield ()
  }

  def getCoordinate: IO[Coordinate] = {
    val reader = new Reader(size)
    for {
      row <- reader.readInt("row")
      col <- reader.readInt("col")
    } yield Coordinate(row, col)
  }

  def getGameState: IO[Result] =
    ref.get.flatMap(map => IO(GameStateChecker.getGameState(map, size)))
}
