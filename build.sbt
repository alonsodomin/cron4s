name := "cron4s"
organization := "com.github.alonsodomin"

scalaVersion := "2.11.7"

version := "0.1.0-SNAPSHOT"

scalacOptions ++= Seq(
  "-language:postfixOps",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Xfuture",
  "-Ywarn-dead-code",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials"
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)

libraryDependencies ++= Seq(
  "com.github.mpilquist"   %%% "simulacrum"              % "0.5.0",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.parboiled"          %%% "parboiled"               % "2.1.0",
  "org.scalacheck"         %%% "scalacheck"              % "1.12.5" % Test
)

//enablePlugins(ScalaJSPlugin)

initialCommands in console := "import cron4s.core._;import cron4s.expr._;import CronField._"