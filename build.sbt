ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "hw5",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.15" % Test,
      "org.scalamock" %% "scalamock" % "5.2.0"  % Test,
      "org.typelevel" %% "cats-core" % "2.9.0"
    ),
    addCompilerPlugin("org.typelevel" % "kind-projector"     % "0.13.2" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"   %% "better-monadic-for" % "0.3.1")
  )
