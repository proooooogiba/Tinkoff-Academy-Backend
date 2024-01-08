ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val catsVersion = "2.9.0"
val catsEffect3 = "3.4.8"
val scalatestVersion = "3.2.15"
val scalamockVersion = "5.2.0"

lazy val root = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-effect" % catsEffect3,
      "org.scalatest" %% "scalatest" % scalatestVersion % Test,
      "org.scalamock" %% "scalamock" % scalamockVersion % Test
    ),
    name := "pets-store"
  )
