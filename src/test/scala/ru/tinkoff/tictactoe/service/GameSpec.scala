package ru.tinkoff.tictactoe.service

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.{IO, Ref}
import org.scalatest.EitherValues
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.tictactoe.model.Cells.{Coordinate, State}
import ru.tinkoff.tictactoe.model.{Continue, O, X}

class GameSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with EitherValues {

  "getBoardString" - {
    "return board state string" in {
      val size = 2
      val boardState = Map(
        Coordinate(0, 0) -> State(Some(X)),
        Coordinate(0, 1) -> State(Some(O)),
        Coordinate(1, 0) -> State(None),
        Coordinate(1, 1) -> State(None),
      )
      val expected = s"""X O \n_ _ """

      val stringBoard = for {
        board <- Ref.of[IO, Map[Coordinate, State]](boardState)
        game = new Game(board, size)
        boardString <- game.getBoardString
      } yield boardString

      stringBoard.asserting(_ shouldEqual expected)
    }
  }

  "getGameState" - {
    "return board current state" in {
      val size = 2
      val boardState = Map(
        Coordinate(0, 0) -> State(Some(X)),
        Coordinate(0, 1) -> State(Some(O)),
        Coordinate(1, 0) -> State(None),
        Coordinate(1, 1) -> State(None),
      )

      val curBoardState = for {
        board <- Ref.of[IO, Map[Coordinate, State]](boardState)
        game = new Game(board, size)
        boardState <- game.getGameState
      } yield boardState

      curBoardState.asserting(_ shouldEqual Continue)
    }
  }

  "makeTurn" - {
    "return State of coordinate" in {
      val size = 2
      val boardState = Map(
        Coordinate(0, 0) -> State(Some(X)),
        Coordinate(0, 1) -> State(Some(O)),
        Coordinate(1, 0) -> State(None),
        Coordinate(1, 1) -> State(None),
      )

      val curBoardState = for {
        board <- Ref.of[IO, Map[Coordinate, State]](boardState)
        game = new Game(board, size)
        boardState <- game.makeTurn(Coordinate(1, 1), X)
      } yield boardState

      curBoardState.asserting(_ shouldEqual Right(State(Some(X))))
    }

    "return State of none" in {
      val size = 2
      val boardState = Map(
        Coordinate(0, 0) -> State(Some(X)),
        Coordinate(0, 1) -> State(Some(O)),
        Coordinate(1, 0) -> State(None),
        Coordinate(1, 1) -> State(None),
      )

      val curBoardState = for {
        board <- Ref.of[IO, Map[Coordinate, State]](boardState)
        game = new Game(board, size)
        boardState <- game.makeTurn(Coordinate(2, 2), X)
      } yield boardState

      curBoardState.asserting(_ shouldEqual Left("Error when getting position by coordinate"))
    }

    "return Error" in {
      val size = 2
      val boardState = Map(
        Coordinate(0, 0) -> State(Some(X)),
        Coordinate(0, 1) -> State(Some(O)),
        Coordinate(1, 0) -> State(None),
        Coordinate(1, 1) -> State(None),
      )

      val curBoardState = for {
        board <- Ref.of[IO, Map[Coordinate, State]](boardState)
        game = new Game(board, size)
        boardState <- game.makeTurn(Coordinate(0, 1), X)
      } yield boardState

      curBoardState.asserting(
        _ shouldEqual Left(
          s"Current cell is already engaged, enter coordinates again",
        ),
      )
    }
  }
}
