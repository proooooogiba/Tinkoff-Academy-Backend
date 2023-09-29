package mark

import cats.data.NonEmptyList

import java.io.{File, FileWriter}
import scala.io.Source
import scala.util.Using

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

  def apply(students: List[Student]): Either[Throwable, Statistics] = {
    Either.cond(
      students.nonEmpty, {
        val originStudents = students
        val marks          = students.groupBy(_.name).mapValues(marks => marks.map(_.mark).sum)
        val studentNames =
          students.map(_.name).distinct.sortBy(name => marks(name))(Ordering[Int].reverse)
        val totalScore   = students.map(_.mark).sum
        val sortedScores = students.map(_.mark).sorted
        val medianScore = {
          val n   = sortedScores.size
          val mid = n / 2
          if (n % 2 == 0) {
            scala.math.max(sortedScores(mid - 1), sortedScores(mid))
          } else {
            sortedScores(mid)
          }
        }
        val mostFrequentScore = students
          .map(_.mark)
          .groupBy(identity)
          .maxBy { case (mark, groupOfMark) => (groupOfMark.size, mark) }
          ._1
        new Statistics {
          val students          = NonEmptyList.fromListUnsafe(studentNames)
          val sum: Int          = totalScore
          val median: Int       = medianScore
          val mostFrequent: Int = mostFrequentScore

          override def sumOpt(student: String): Option[Int] = {
            val sumOfMarks = originStudents.filter(_.name == student).map(_.mark).sum
            if (sumOfMarks > 0) Some(sumOfMarks) else None
          }

          override def avgOpt(student: String): Option[Double] = {
            val quantity = originStudents.count(_.name == student)
            sumOpt(student).flatMap(sumOfMarks =>
              if (quantity != 0) Some(sumOfMarks.toDouble / quantity) else None
            )
          }
        }
      },
      new Exception("list can't be empty")
    )
  }

  def apply(inputFileName: String): Either[Throwable, Statistics] = {
    readStudentsFromFile(inputFileName) match {
      case Right(students) if students.isEmpty  => Left(new Exception("students can't be empty"))
      case Right(students) if students.nonEmpty => apply(students)
      case Left(error) => Left(new RuntimeException(s"Failed to read students from file: $error"))
    }
  }

  def calculate(
      inputFileName: String,
      outputFileName: String
  ): Either[Throwable, Unit] = {
    for {
      statistics <- apply(inputFileName).left.map(error =>
        new RuntimeException(s"Failed to get statistics: $error")
      )
      output = generateOutput(statistics)
      _ <- writeStatsToFile(outputFileName, output).left.map(error =>
        new Exception(s"can't write stats to file $error")
      )
    } yield ()
  }

  private def readStudentsFromFile(inputFileName: String): Either[Throwable, List[Student]] = {
    Using(Source.fromFile(inputFileName)) { source =>
      val students = (for {
        line <- source.getLines()
        listOfStudent = line.split("\t").toList
        mark    <- listOfStudent.last.toIntOption
        student <- if (listOfStudent.length == 2) Some(Student(listOfStudent.head, mark)) else None
      } yield student).toList
      source.close()
      students
    }.toEither
  }
  private def writeStatsToFile(outputFileName: String, output: String): Either[Throwable, Unit] = {
    val file = new File(outputFileName)
    if (file.exists()) {
      Left(new RuntimeException(s"File $outputFileName already exists"))
    } else {
      Using(new FileWriter(file)) { fileWriter =>
        fileWriter.write(output)
        fileWriter.close()
      }.toEither
    }
  }

  private def generateOutput(statistics: Statistics): String = {
    val sum          = statistics.sum
    val median       = statistics.median
    val mostFrequent = statistics.mostFrequent

    val studentScores = for {
      student <- statistics.students.toList
      sumOpt = statistics.sumOpt(student).getOrElse(0)
      avgOpt = statistics.avgOpt(student).getOrElse(0.0)
    } yield f"$student\t$sumOpt\t$avgOpt%.2f"

    s"$sum\t$median\t$mostFrequent\n${studentScores.mkString("\n")}"
  }
}

object app extends App {
  val statistics = Statistics.calculate("input_1.tsv", "output_test.tsv")
}
