ThisBuild / scalaVersion := "2.13.11"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test

lazy val lecture = project
lazy val homework = (project in file("."))

