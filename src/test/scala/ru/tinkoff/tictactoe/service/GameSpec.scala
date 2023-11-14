package ru.tinkoff.tictactoe.service

import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Ref}
import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.tictactoe.model.Cells.{Coordinate, State}
import ru.tinkoff.tictactoe.model.{Continue, O, X}

class GameSpec extends AnyFlatSpec with Matchers with EitherValues with MockFactory {
  "getBoardString" should "return board state string" in {
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

    stringBoard.unsafeRunSync() shouldEqual expected
  }

  "getGameState" should "return board current state" in {
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

    curBoardState.unsafeRunSync() shouldEqual Continue
  }

  "makeTurn" should "return State of coordinate" in {
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

    curBoardState.unsafeRunSync() shouldEqual Right(State(Some(X)))
  }

  it should "return State of none" in {
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

    curBoardState.unsafeRunSync() shouldEqual Left("Error when getting position by coordinate")
  }

  it should "return Error" in {
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

    curBoardState.unsafeRunSync() shouldEqual Left(
      s"Current cell is already engaged, enter coordinates again",
    )
  }

}
