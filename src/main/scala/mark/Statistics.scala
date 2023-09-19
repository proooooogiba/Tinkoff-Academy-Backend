package mark

import cats.data.NonEmptyList

trait Statistics {
  def sumOpt(student: String): Option[Int]

  def avgOpt(student: String): Option[Double]

  def students: NonEmptyList[String]

  def sum: Int

  def median: Int

  def mostFrequent: Int
}

object Statistics {
  // students - сырые (не обработанные) данные из файла.
  def apply(students: List[Student]): Either[Throwable, Statistics] = ???

  def apply(inputFileName: String): Either[Throwable, Statistics] = ???

  def calculate(
      inputFileName: String,
      outputFileName: String
  ): Either[Throwable, Unit] = ???
}
