package mark

import cats.data.NonEmptyList

import java.io.{File, FileWriter}
import scala.io.Source
import scala.util.Try

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
    try {
      val originStudents = students

      val marks = originStudents
        .groupBy(_.name)
        .view
        .mapValues(marks => marks.map(_.mark).sum)

      val studentNames = originStudents.view
        .map(_.name)
        .distinct
        .toList
        .sortBy(name => marks(name))(Ordering[Int].reverse)

      val totalScore = originStudents.map(_.mark).sum

      val sortedScores = originStudents.map(_.mark).sorted
      val medianScore = {
        val n   = sortedScores.size
        val mid = n / 2
        if (n % 2 == 0) {
          scala.math.max(sortedScores(mid - 1), sortedScores(mid))
        } else {
          sortedScores(mid)
        }
      }

      val mostFrequentScore = originStudents
        .map(_.mark)
        .groupBy(identity)
        .maxBy { case (mark, groupOfMark) => (groupOfMark.size, mark) }
        ._1

      val statistics = new Statistics {
        val students: NonEmptyList[String] = NonEmptyList.fromListUnsafe(studentNames)
        val sum: Int                       = totalScore
        val median: Int                    = medianScore
        val mostFrequent: Int              = mostFrequentScore

        override def sumOpt(student: String): Option[Int] = {
          val sumOfMarks = originStudents.map { studentFromList =>
            if (studentFromList.name == student) studentFromList.mark else 0
          }.sum
          if (sumOfMarks > 0) Some(sumOfMarks) else None
        }

        override def avgOpt(student: String): Option[Double] = {
          val quantity = originStudents.count(_.name == student)
          sumOpt(student) match {
            case Some(sumOfMarks) if quantity != 0 => Some(sumOfMarks.toDouble / quantity)
            case _                                 => None
          }
        }
      }
      Right(statistics)
    } catch {
      case throwable: Throwable => Left(throwable)
    }
  }

  def apply(inputFileName: String): Either[Throwable, Statistics] = {
    try {
      val students = readStudentsFromFile(inputFileName) match {
        case Right(students) if students.isEmpty  => throw new Exception("students can't be empty")
        case Right(students) if students.nonEmpty => students
        case Left(error) => throw new RuntimeException(s"Failed to read students from file: $error")
      }
      apply(students)
    } catch {
      case throwable: Throwable => Left(throwable)
    }
  }

  def calculate(
      inputFileName: String,
      outputFileName: String
  ): Either[Throwable, Unit] = {
    try {
      val statistics: Statistics = apply(inputFileName) match {
        case Right(stats) => stats
        case Left(error)  => throw new RuntimeException(s"Failed to get statistics: $error")
      }
      val output = generateOutput(statistics)
      writeStatsToFile(outputFileName, output) match {
        case Right(value) =>
        case Left(error)  => throw new Exception(s"can't write stats to file $error")
      }
      Right()
    } catch {
      case throwable: Throwable => Left(throwable)
    }
  }

  private def readStudentsFromFile(inputFileName: String): Either[Throwable, List[Student]] = {
    try {
      val source = Source.fromFile(inputFileName)

      val students = (for {
        line <- source.getLines()
        listOfStudent = line.split("\t").toList
        mark    <- Try(listOfStudent.last.toInt).toOption
        student <- if (listOfStudent.length == 2) Some(Student(listOfStudent.head, mark)) else None
      } yield student).toList

      source.close()
      Right(students)
    } catch {
      case throwable: Throwable => Left(throwable)
    }
  }

  private def writeStatsToFile(outputFileName: String, output: String): Either[Throwable, Unit] = {
    val file = new File(outputFileName)
    if (file.exists()) {
      throw new Exception("Shouldn't rewrite file")
    }
    val fileWriter = new FileWriter(file)
    fileWriter.write(output)
    fileWriter.close()
    Right()
  }

  private def generateOutput(statistics: Statistics): String = {
    val sum          = statistics.sum
    val median       = statistics.median
    val mostFrequent = statistics.mostFrequent

    val studentScores = for {
      student <- statistics.students.toList
      sumOpt = statistics.sumOpt(student).get
      avgOpt = statistics.avgOpt(student).get
    } yield f"$student\t$sumOpt\t$avgOpt%.2f"

    s"$sum\t$median\t$mostFrequent\n${studentScores.mkString("\n")}"
  }
}

object app extends App {
  val statistics = Statistics.calculate("input_2.tsv", "output_existed_file.tsv")
}
