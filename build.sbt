ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "OilParserAPI"
  )

val AkkaVersion = "2.7.0"
val AkkaHttpVersion = "10.5.0"
val circeVersion = "0.14.3"

libraryDependencies ++= Seq(

  /* akka dependencies */
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,

  /* akka testing dependencies */
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion,

  "com.github.pureconfig" %% "pureconfig" % "0.17.2",

  /* csv parsing dependencies */
  "com.nrinaudo" %% "kantan.csv" % "0.7.0",
  "com.nrinaudo" %% "kantan.csv-generic" % "0.7.0",
  "com.nrinaudo" %% "kantan.csv-commons" % "0.7.0",

  /* encode/decode dependencies */
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeVersion,

  "org.scalatest" %% "scalatest" % "3.2.15" % Test
)