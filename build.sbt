ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

val catsVersion = "2.9.0"
val catsEffect3 = "3.4.8"
val scalatestVersion = "3.2.17"
val scalamockVersion = "5.2.0"
val tapirVersion = "1.7.6"
val http4sVersion = "0.23.23"
val logbackVersion = "1.4.11"
val tethysVersion = "0.26.0"
val enumeratumVersion = "1.7.2"
val flywayVersion = "9.16.0"
val doobieVersion = "1.0.0-RC2"
val testcontainersScalatestVersion = "0.40.12"
val testcontainersPostgresqlVersion = "0.40.12"
val pureConfigVersion = "0.17.4"
val quillVersion = "4.6.0"
val ceTestingVersion = "1.5.0"

lazy val root = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      // cats
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-effect" % catsEffect3,

      // tapir
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-tethys" % tapirVersion,

      // http4s
      "org.http4s" %% "http4s-ember-server" % http4sVersion,

      // logback
      "ch.qos.logback" % "logback-classic" % logbackVersion,

      // tethys
      "com.tethys-json" %% "tethys-core" % tethysVersion,
      "com.tethys-json" %% "tethys-jackson" % tethysVersion,
      "com.tethys-json" %% "tethys-derivation" % tethysVersion,
      "com.tethys-json" %% "tethys-enumeratum" % tethysVersion,

      // enumeratum
      "com.beachape" %% "enumeratum" % enumeratumVersion,

      // doobie + quill
      "io.getquill" %% "quill-doobie" % quillVersion,
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,

      // pureconfig
      "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,

      // flyway
      "org.flywaydb" % "flyway-core" % flywayVersion,

      // test
      "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % tapirVersion % Test,
      "org.scalatest" %% "scalatest" % scalatestVersion % Test,
      "org.scalamock" %% "scalamock" % scalamockVersion % Test,

      // integration test
      "org.typelevel" %% "cats-effect-testing-scalatest" % ceTestingVersion % IntegrationTest,
      "com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainersScalatestVersion % IntegrationTest,
      "com.dimafeng" %% "testcontainers-scala-postgresql" % testcontainersPostgresqlVersion % IntegrationTest,
    ),
    name := "news-aggregator",
  )
  .enablePlugins(JavaAppPackaging)
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    IntegrationTest / fork := true,
  )
  .settings(
    Compile / run / fork := true,
  )
