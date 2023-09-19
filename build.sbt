ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "hw3",
    libraryDependencies ++= Seq(
      "org.typelevel"     %% "cats-core"       % "2.9.0",
      "org.scalatest"     %% "scalatest"       % "3.2.15"   % Test,
      "org.scalamock"     %% "scalamock"       % "5.2.0"    % Test,
      "org.scalatestplus" %% "scalacheck-1-17" % "3.2.15.0" % Test
    )
  )
