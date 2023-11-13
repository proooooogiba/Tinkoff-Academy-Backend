package ru.tinkoff.tictactoe

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.std.Env
import cats.effect.{ExitCode, IO, IOApp, Ref}
import cats.instances.string._
import ru.tinkoff.tictactoe.Cells.{Coordinate, State}
import ru.tinkoff.tictactoe.model.{Player, Size}

object X extends Player {
  override def nextPlayer(): Player = O
  override def toString: String = "X"
}
object O extends Player {
  override def nextPlayer(): Player = X
  override def toString: String = "O"
}

sealed trait Result
case object Continue extends Result
case object Draw extends Result
case class Winner(player: Player) extends Result

object Cells {
  final case class Coordinate(row: Int, col: Int)
  case class Board(size: Int, positions: Map[Coordinate, State])

  final case class State(player: Option[Player])
}

final class TicTacToe(ref: Ref[IO, Map[Coordinate, State]], size: Int) {

  def getState(coordinate: Coordinate): IO[State] =
    ref.get.map(_.getOrElse(coordinate, State(None)))

  def makeTurn(coordinate: Coordinate, player: Player): IO[State] =
    getState(coordinate).flatMap { state =>
      state.player match {
        case Some(_) => IO(State(None))
        case None =>
          ref.modify { allPositions =>
            val mayBePosition = allPositions.get(coordinate).map { position =>
              position.copy(player = Some(player))
            }
            val newPositions = allPositions ++ mayBePosition.map(m => (coordinate, m))
            (newPositions, mayBePosition.get)
          }
      }
    }

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
    if (num >= size) IO.raiseError(new RuntimeException(s"Number should be less $size"))
    else IO(num)

  def getCoordinate: IO[Coordinate] = for {
    row <- readInt("row")
    col <- readInt("col")
  } yield Coordinate(row, col)

  def checkResult: IO[Result] =
    ref.get.map(gameMap =>
      checkWinner(gameMap) match {
        case None         => if (checkDraw(gameMap)) Draw else Continue
        case Some(player) => Winner(player)
      },
    )

  def checkDraw(gameMap: Map[Coordinate, State]): Boolean = !gameMap.values.exists(_.player.isEmpty)

  def checkWinner(gameMap: Map[Coordinate, State]): Option[Player] = {
    def checkRowWin(row: Int): Option[Player] = {
      val states = (0 until size).map(col => gameMap.getOrElse(Coordinate(row, col), State(None)))
      if (states.forall(_.player.isDefined) && states.map(_.player.get).toSet.size == 1) {
        states.head.player
      } else {
        None
      }
    }

    def checkColWin(col: Int): Option[Player] = {
      val states = (0 until size).map(row => gameMap.getOrElse(Coordinate(row, col), State(None)))
      if (states.forall(_.player.isDefined) && states.map(_.player.get).toSet.size == 1) {
        states.head.player
      } else {
        None
      }
    }

    def checkDiagonalWin(): Option[Player] = {
      val mainDiagonalStates =
        (0 until size).map(i => gameMap.getOrElse(Coordinate(i, i), State(None)))
      val antiDiagonalStates =
        (0 until size).map(i => gameMap.getOrElse(Coordinate(i, size - 1 - i), State(None)))

      def checkDiagonal(states: Seq[State]): Option[Player] =
        if (states.forall(_.player.isDefined) && states.map(_.player.get).toSet.size == 1) {
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

object Game extends IOApp {
  def run(args: List[String]): IO[ExitCode] = for {
    size <- getSize[IO]
    _ <- IO.println(s"размер поля - ${size.value}")
    positions = (for {
      i <- 0 until size.value
      j <- 0 until size.value
    } yield Coordinate(i, j) -> State(None)).toMap

    boardRef <- Ref.of[IO, Map[Coordinate, State]](positions)

    game = new TicTacToe(boardRef, size.value)
    res <- runGame(game)
    _ <- IO.println(s"$res")
  } yield ExitCode.Success

  def runGame(game: TicTacToe): IO[Result] = {

    def iterationGame(player: Player): IO[Result] = {
      val result = for {
        coordinate <- game.getCoordinate
        _ <- game.makeTurn(coordinate, player)
        _ <- game.printBoard
        res <- game.checkResult
      } yield res

      result.flatMap {
        case Continue       => iterationGame(player.nextPlayer())
        case Winner(player) => IO(Winner(player))
        case Draw           => IO(Draw)
      }
    }

    iterationGame(X)
  }

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
