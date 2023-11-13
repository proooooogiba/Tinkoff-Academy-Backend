//package ru.tinkoff.tictactoe
//
//import cats.effect.{ExitCode, IO, IOApp, Ref}
//import cats.implicits._
//import cats.syntax.all._
//
//import scala.annotation.tailrec
//import scala.io.StdIn

//final class BankAccounts(ref: Ref[IO, Map[String, BankAccount]]) {
//
//  def alterAmount(accountNumber: String, amount: Int): IO[Option[Balance]] =
//    ref.modify { allBankAccounts =>
//      val maybeBankAccount = allBankAccounts.get(accountNumber).map { bankAccount =>
//        bankAccount.copy(balance = bankAccount.balance + amount)
//      }
//      val newBankAccounts = allBankAccounts ++ maybeBankAccount.map(m => (m.number, m))
//      val maybeNewBalance = maybeBankAccount.map(_.balance)
//      (newBankAccounts, maybeNewBalance)
//    }
//
//  def getBalance(accountNumber: String): IO[Option[Balance]] =
//    ref.get.map(_.get(accountNumber).map(_.balance))
//
//  def addAccount(account: BankAccount): IO[Unit] =
//    ref.update(_ + (account.number -> account))
//}
//
//

//object Main extends IOApp {
//  override def run(args: List[String]): IO[ExitCode] =
//    for {
//      _ <- example
//    } yield ExitCode.Success
//}

//object Cells {
//  type CellState = Int
////  final case class Cell(coordinate: Coordinate, player: Player)
//  final case class Coordinate(x: Int, y: Int)
//  case class Board(size: Int, positions: Map[Coordinate, Player])
//}
//
//import ru.tinkoff.tictactoe.Cells._
//
//
//
//final class TicTacToe(ref: Ref[IO, Map[Coordinate, Player]]) {
//
//  def getState(coordinate: Coordinate): IO[Option[Player]] =
//    ref.get.map(_.get(coordinate))
//
////  Cell => IO[Option[CellState]] -> (Cell) => IO[Option[CellState]]
//
//  def makeTurn(cell: Cell): IO[Option[Player]] =
//    getState(cell.coordinate).flatMap {
//      case Some(value) => getState(cell.coordinate)
//      case None =>
//        ref.update(_ + (cell.coordinate -> cell.player))
//        getState(cell.coordinate)
//    }
//
//  def isOver(): Boolean =
//    if (ref.get.map(m => m.size) == IO.pure(9)) {
//      true
//    } else {
//      false
//    }
//}
//
//trait Player {
//  def nextPlayer(): Player
//}
//
//trait firstPlayer extends Player
//trait secondPlayer extends Player
//
//object X extends firstPlayer {
//  override def nextPlayer(): Player = O
//}
//object O extends secondPlayer {
//  override def nextPlayer(): Player = X
//}
//
//object Main extends IOApp {
//
//  def gameProcess(game: TicTacToe): Unit = {
//    val x = StdIn.readLine().toInt
//    val y = StdIn.readLine().toInt
//    val coordinate = Coordinate(x, y)
//
//    val cell = Cell(coordinate, X)
//
//    println(game.getState(coordinate))
//
//    def isValidMove(row: Int, col: Int, state: Ref[IO, GameState]): IO[Boolean] =
//      state.get.map { gameState =>
//        gameState.board.indices.contains(row) && gameState.board(row).indices.contains(col) &&
//        gameState.board(row)(col).isEmpty
//      }
//
//    @tailrec
//    def gameIteration(turn: IO[Option[Player]], player: Player): IO[Option[Player]] = {
////      if (game.isOver()) {
////
////      }
//
//      val x = StdIn.readLine().toInt
//      val y = StdIn.readLine().toInt
//      val coordinate = Coordinate(x, y)
//      val cell = Cell(coordinate, player)
//
//      println(game.getState(coordinate))
//
//      if (game.getState(coordinate) != game.makeTurn(cell)) {
//        gameIteration(game.makeTurn(cell), player.nextPlayer())
//      } else {
//        gameIteration(game.makeTurn(cell), player)
//      }
//    }
//
//    gameIteration(game.makeTurn(cell), X)
//  }
//
//  override def run(args: List[String]): IO[ExitCode] =
////    val example = for {
////      ref <- Ref[IO].of(Map.empty[Coordinate, CellState])
////      ticTacToe = new TicTacToe(ref)
////      _ <- ticTacToe.getState(Coordinate(1, 1))
////      _ <- ticTacToe.makeTurn(Cell(Coordinate(1, 1), 1))
////      ending <- ticTacToe.makeTurn(Cell(Coordinate(1, 1), 2))
////      _ <- ticTacToe.makeTurn(Cell(Coordinate(1, 2), 1))
////      ending <- ticTacToe.getState(Coordinate(1, 1))
////    } yield println(ending)
//    Ref[IO]
//      .of(Map.empty[Coordinate, Player])
//      .map { ref =>
//        gameProcess(new TicTacToe(ref))
//        ExitCode.Success
//      }
//
////    val example = for {
////      ref <- Ref[IO].of(Map.empty[Coordinate, Player])
////      ticTacToe = new TicTacToe(ref)
////      result <- gameProcess(ticTacToe)
////    } yield println(result)
//
////    for {
////      _ <- gameProcess()
////    } yield ExitCode.Success
//
//}
//
////object  {
////  private final val MinValue = 3
////  def fromInt(value: Int): Option[Size] =
////    if (value >= MinValue) Some(new Size(value)) else None
////}
//
////object Main extends IOApp {
////  override def run(args: List[String]): IO[ExitCode] =
////    for {
////      size <- getSize[IO]
////      _ <- IO.println(s"размер поля - ${size.value}")
////
////    } yield ExitCode.Success
////
////  private def getSize[F[_]: Env: MonadThrow]: F[Size] =
////    OptionT(Env[F].get("MAP_SIZE"))
////      .toRight("MAP_SIZE not found")
////      .subflatMap(ss =>
////        ss.toIntOption.toRight(s"Expected int in MAP_SIZE env variable, but got $ss"),
////      )
////      .subflatMap(si => Size.fromInt(si).toRight(s"Size can be equal to 3 or more, but not $si"))
////      .leftMap(new IllegalArgumentException(_))
////      .rethrowT
////}
//
////          _ <- ticTacToe.getState(Coordinate(1, 1))
////          _ <- ticTacToe.makeTurn(Cell(Coordinate(1, 1), 1))
////          ending <- ticTacToe.makeTurn(Cell(Coordinate(1, 1), 2))
////          _ <- ticTacToe.makeTurn(Cell(Coordinate(1, 2), 1))
////          ending <- ticTacToe.getState(Coordinate(1, 1))


