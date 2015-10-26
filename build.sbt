name := "cron4s"
organization := "com.github.alonsodomin"

scalaVersion := "2.11.7"

version := "0.1.0-SNAPSHOT"

scalacOptions ++= Seq(
  "-language:postfixOps",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Ywarn-dead-code"
)

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scalacheck"         %% "scalacheck"               % "1.12.5" % Test
)

initialCommands in console := "import cron4s.expr._"