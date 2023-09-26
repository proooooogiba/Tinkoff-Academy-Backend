import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.io.Source
import scala.util.Using
import scala.util.matching.Regex

object Homework {

  def main(args: Array[String]): Unit = {
    val constPattern: Regex = "Value\\((-?\\d+)\\)".r

    val matchPattern: String => Either[Exception, Int] = {
      case constPattern(i) => Right(i.toInt)
      case _               => Left(new Exception)
    }

    Using(Source.fromFile("file.txt")) { s =>
      s.getLines().toSeq.map(s => s)
      Files.write(Paths.get("file.txt"), "file contents".getBytes(StandardCharsets.UTF_8))
    }
  }

}
