package mark

import cats.data.NonEmptyList
import cats.implicits.catsSyntaxOptionId
import org.scalacheck.Gen
import org.scalacheck.Gen.alphaNumChar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

import java.io.File
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

// Don't change this file
class StatisticsUpstreamSpec extends AnyFlatSpec with Matchers with ScalaCheckDrivenPropertyChecks {
  "Statistics init" should "not from empty students" in {
    Statistics(List.empty).isLeft shouldBe true
  }

  it should "from students" in {
    val students = List(Student("Примеров Пример", 7))

    Statistics(students).isRight shouldBe true
  }

  it should "from file" in {
    Statistics("input_1.tsv").isRight shouldBe true
  }

  it should "not from nonexistent file" in {
    Statistics("abcd.tsv").isLeft shouldBe true
  }

  "Statistics from file" should "read correct students" in {
    Statistics("input_1.tsv").succeed.students shouldBe NonEmptyList.fromListUnsafe(
      List("Примеров Пример Примерович", "Примеров Пример")
    )
  }

  it should "ignore wrong format" in {
    Statistics("input_2.tsv").succeed.students shouldBe NonEmptyList.fromListUnsafe(
      List("Примеров Пример Примерович")
    )
  }

  it should "not create from corrupt file" in {
    Statistics("input_3.tsv").isLeft shouldBe true
  }

  it should "not create from empty file" in {
    Statistics("input_4.tsv").isLeft shouldBe true
  }

  "Statistics calculate" should "correctly read and write to files" in {
    Statistics.calculate("input_1.tsv", "output_2.tsv").succeed

    val result = for {
      expected <- Using(Source.fromFile("output_1.tsv"))(_.getLines().mkString("\n").trim)
      actual   <- Using(Source.fromFile("output_2.tsv"))(_.getLines().mkString("\n").trim)
      _        <- Try(new File("output_2.tsv").deleteOnExit())
    } yield (expected, actual)

    result match {
      case Success((expected, actual)) => actual shouldBe expected
      case Failure(exception)          => fail(exception)
    }
  }

  it should "not rewrite file" in {
    Statistics.calculate("input_1.tsv", "output_existed_file.tsv").isLeft shouldBe true
  }

  it should "not write to wrong path" in {
    Statistics.calculate("input_1.tsv", "./test path\\/not_existed_path.tsv").isLeft shouldBe true
  }

  "Statistics" should "return total sum for one student" in {
    val students = List(Student("Примеров Пример", 7))

    Statistics(students).map(_.sum) shouldBe Right(7)
  }

  it should "return sum for one student" in {
    val studentName = "Примеров Пример"
    val students    = List(Student(studentName, 7))

    Statistics(students).toOption
      .flatMap(_.sumOpt(studentName)) shouldBe Some(7)
  }

  it should "not return sum for unknown student" in {
    val studentName = "Примеров Пример"
    val students    = List(Student(studentName, 7))

    Statistics(students).toOption
      .flatMap(_.sumOpt("Примеров")) shouldBe None
  }

  it should "not duplicate names" in {
    Statistics(studentsTwo).succeed.students shouldBe NonEmptyList.fromListUnsafe(
      List("Adam", "Eve", "Lilith")
    )
  }

  it should "get correct sum for student" in {
    val stats = Statistics(studentsTwo).succeed

    stats.sumOpt("Lilith") shouldBe Some(5)
    stats.sumOpt("Adam") shouldBe Some(19)
    stats.sumOpt("Abel") shouldBe None
  }

  it should "get correct avg for student" in {
    val stats = Statistics(studentsTwo).succeed

    stats.avgOpt("Lilith") shouldBe Some(5d)
    stats.avgOpt("Adam") shouldBe Some(9.5d)
    stats.avgOpt("Eve") shouldBe Some(9d)
    stats.avgOpt("Abel") shouldBe None
  }

  it should "return correct sum" in {
    Statistics(studentsTwo).succeed.sum shouldBe studentsTwo.map(_.mark).sum
  }

  it should "return correct median (odd size)" in {
    Statistics(studentsTwo).succeed.median shouldBe 9
  }

  it should "return correct median (even size)" in {
    Statistics(Student("Abel", 3) +: studentsTwo).succeed.median shouldBe 9
  }

  it should "return correct most frequent" in {
    Statistics(studentsTwo).succeed.mostFrequent shouldBe 10
  }

  it should "return correct avg for any students" in {
    forAll(generateStudents) { students: List[Student] =>
      val stats = Statistics(students).succeed
      val avgs =
        students
          .groupBy(_.name)
          .view
          .mapValues(marks => marks.map(_.mark).sum.doubleValue / marks.length)
          .mapValues(_.some)
          .toList

      avgs.map(_._1).map(stats.avgOpt) shouldBe avgs.map(_._2)
    }
  }

  it should "return correct sum for any students" in {
    forAll(generateStudents) { students: List[Student] =>
      val stats = Statistics(students).succeed
      val sums =
        students
          .groupBy(_.name)
          .view
          .mapValues(marks => marks.map(_.mark).sum)
          .mapValues(_.some)
          .toList

      sums.map(_._1).map(stats.sumOpt) shouldBe sums.map(_._2)
    }
  }

  it should "return correct students names" in {
    implicit val reverseOrder: Ordering[Int] = Ordering.Int.reverse

    forAll(generateStudents) { students: List[Student] =>
      val stats = Statistics(students).succeed
      val marks =
        students
          .groupBy(_.name)
          .view
          .mapValues(marks => marks.map(_.mark).sum)

      val names = students.view
        .map(_.name)
        .distinct
        .toList
        .sortBy(marks(_))

      stats.students shouldBe NonEmptyList.fromListUnsafe(names)
    }
  }

  it should "return correct sum for all students" in {
    forAll(generateStudents) { students: List[Student] =>
      val stats = Statistics(students).succeed

      stats.sum shouldBe students.map(_.mark).sum
    }
  }

  it should "return correct median for all students" in {
    forAll(generateStudents) { students: List[Student] =>
      val stats = Statistics(students).succeed
      val marks = students.map(_.mark).sorted

      stats.median shouldBe marks(students.length / 2)
    }
  }

  it should "return correct most frequent for all students" in {
    forAll(generateStudents) { students: List[Student] =>
      val stats = Statistics(students).succeed
      val mostFrequent =
        students
          .map(_.mark)
          .groupBy(identity)
          .maxBy { case (mark, list) => (list.length, mark) }
          ._1

      stats.mostFrequent shouldBe mostFrequent
    }
  }

  implicit final class EitherTestOps[E, A](val value: Either[E, A]) {
    def succeed: A =
      value.left.map(e => fail(s"Unexpected error $e")).merge

    def failed: E =
      value.map(result => fail(s"Unexpected result $result")).merge
  }

  private lazy val studentsTwo = List(
    Student("Eve", 8),
    Student("Lilith", 5),
    Student("Adam", 9),
    Student("Eve", 10),
    Student("Adam", 10)
  )

  private def generateStudentMark(name: String): Gen[List[Student]] =
    Gen.nonEmptyListOf(Gen.choose(1, 10)).map(_.map(Student(name, _)))

  private def generateStudents: Gen[List[Student]] =
    Gen
      .choose(1, 50)
      .flatMap(Gen.listOfN[String](_, Gen.stringOfN(15, alphaNumChar)))
      .flatMap(studentsNames =>
        Gen.sequence[List[List[Student]], List[Student]](studentsNames.map(generateStudentMark))
      )
      .map(_.flatten)
}
