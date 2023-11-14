package ru.tinkoff.tictactoe

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.std.Env
import cats.effect.{ExitCode, IO, IOApp, Ref}
import cats.instances.string._
import ru.tinkoff.tictactoe.controller.GameController
import ru.tinkoff.tictactoe.model.Cells.{Coordinate, State}
import ru.tinkoff.tictactoe.model.{Draw, Size, Winner}
import ru.tinkoff.tictactoe.service.Game

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = for {
    size <- getSize[IO]
    _ <- IO.println(s"Board size is - ${size.value}")
    positions = (for {
      i <- 0 until size.value
      j <- 0 until size.value
    } yield Coordinate(i, j) -> State(None)).toMap

    boardRef <- Ref.of[IO, Map[Coordinate, State]](positions)
    game = new Game(boardRef, size.value)
    controller = new GameController(game)
    result <- controller.runGame().map {
      case Winner(player) => s"$player has won! Congratulations!"
      case Draw           => s"Draw! Friendship has won!"
      case _ => IO.raiseError(new IllegalArgumentException(s"Error! Impossible result of game!"))
    }
    _ <- IO.println(result)
  } yield ExitCode.Success

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
