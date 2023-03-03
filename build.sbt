ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "OilParserAPI"
  )

val AkkaVersion = "2.7.0"
val AkkaHttpVersion = "10.5.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.github.pureconfig" %% "pureconfig" % "0.17.2",
  "com.nrinaudo" %% "kantan.csv" % "0.7.0",
  "com.nrinaudo" %% "kantan.csv-generic" % "0.7.0",
  "com.nrinaudo" %% "kantan.csv-commons" % "0.7.0"

)