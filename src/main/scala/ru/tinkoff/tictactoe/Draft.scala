package ru.tinkoff.tictactoe

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.std.Env
import cats.effect.{ExitCode, IO, IOApp, Ref}
import cats.instances.string._
import ru.tinkoff.tictactoe.Cells.{Coordinate, State}
import ru.tinkoff.tictactoe.model.Size

object Cells {
  final case class Coordinate(row: Int, col: Int)
  case class Board(size: Int, positions: Map[Coordinate, State])

  final case class State(player: Option[Player])
}

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

final class TicTacToe(ref: Ref[IO, Map[Coordinate, State]]) {

  def getState(coordinate: Coordinate): IO[Option[State]] =
    ref.get.map(_.get(coordinate))

  def makeTurn(coordinate: Coordinate, player: Player): IO[Option[State]] =
    getState(coordinate).flatMap {
      case Some(value) =>
        value.player match {
          case Some(_) => IO(None)
          case None =>
            ref.modify { allPositions =>
              val mayBePosition = allPositions.get(coordinate).map { position =>
                position.copy(player = Some(player))
              }
              val newPositions = allPositions ++ mayBePosition.map(m => (coordinate, m))
              (newPositions, mayBePosition)
            }
        }
      case None => IO(None)
    }

  def printBoard: IO[Unit] = {
    def printCellState(state: State): String = state.player.map(_.toString).getOrElse("_")
    for {
      board <- ref.get
      rows = (0 until 3).map { row =>
        (0 until 3).map { col =>
          val coordinate = Coordinate(row, col)
          printCellState(board.getOrElse(coordinate, State(None))) + " "
        }.mkString
      }
      _ <- IO.println(rows.mkString("\n"))
    } yield ()
  }

  private def readInt(name: String): IO[Int] = for {
    input <- IO(println(s"Enter $name number:")) *> IO(scala.io.StdIn.readLine())
    num <- parseInt(input).handleErrorWith(err => IO(println(err)) *> readInt(name))
    checkedNumber <- checkNumber(num).handleErrorWith(err => IO(println(err)) *> readInt(name))
  } yield checkedNumber

  private def parseInt(s: String): IO[Int] = IO
    .delay(s.toInt)
    .handleErrorWith(_ =>
      IO.raiseError(new NumberFormatException("Invalid number, when parsing Int")),
    )

  private def checkNumber(num: Int): IO[Int] =
    if (num > 3) IO.raiseError(new RuntimeException(s"Number should be less 3")) else IO(num)

  def getCoordinate: IO[Coordinate] = for {
    row <- readInt("row")
    col <- readInt("col")
  } yield Coordinate(row, col)

}

object Game extends IOApp {
  def run(args: List[String]): IO[ExitCode] = for {
    size <- getSize[IO]
    _ <- IO.println(s"размер поля - ${size.value}")
    positions = (for {
      i <- 0 until size.value
      j <- 0 until size.value
    } yield Coordinate(i, j) -> State(None)).toMap

    boardRef <- Ref.of[IO, Map[Coordinate, State]](positions)

    game = new TicTacToe(boardRef)
    _ <- game.printBoard
    coordinate <- game.getCoordinate
    _ <- game.makeTurn(coordinate, X)
    _ <- game.printBoard
    coordinate <- game.getCoordinate
    _ <- game.makeTurn(coordinate, O)
    _ <- game.printBoard

  } yield ExitCode.Success

//  def runGame(boardRef: Ref[IO, Board]): IO[Unit] =

  private def getSize[F[_]: Env: MonadThrow]: F[Size] =
    OptionT(Env[F].get("MAP_SIZE"))
      .toRight("MAP_SIZE not found")
      .subflatMap(ss =>
        ss.toIntOption.toRight(s"Expected int in MAP_SIZE env variable, but got $ss"),
      )
      .subflatMap(si => Size.fromInt(si).toRight(s"Size can be equal to 3 or more, but not $si"))
      .leftMap(new IllegalArgumentException(_))
      .rethrowT
}

