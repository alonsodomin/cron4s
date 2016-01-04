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
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.6.3")

libraryDependencies ++= Seq(
  "com.github.mpilquist"   %%% "simulacrum"              % "0.5.0",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.parboiled"          %%% "parboiled"               % "2.1.0",
  "org.spire-math"         %%% "cats"                    % "0.3.0",
  "org.scalamacros"        %% "resetallattrs"            % "1.0.0-M1",
  "org.scalacheck"         %%% "scalacheck"              % "1.12.5" % Test
)

//enablePlugins(ScalaJSPlugin)

initialCommands in console := "import cron4s.core._, cron4s.expr._, cron4s.matcher._, CronField._"