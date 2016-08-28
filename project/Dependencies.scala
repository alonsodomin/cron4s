import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._
import Keys._

object Dependencies {

  object version {
    val simulacrum  = "0.7.0"
    val cats        = "0.6.0"
    val scalaz      = "7.2.1"
    val shapeless   = "2.3.1"

    val momentjs    = "0.1.5"
    val jodaTime    = "2.9.4"
    val jodaConvert = "1.8.1"
    val parserComb  = "1.0.2"

    val scalacheck  = "1.12.5"
    val scalatest   = "3.0.0"
    val discipline  = "0.5"

    val scalaJavaTime = "2.0.0-M3"
  }

  lazy val core = Def.settings {
    libraryDependencies ++= Seq(
      compilerPlugin("org.scalamacros" % "paradise"       % "2.1.0" cross CrossVersion.full),
      compilerPlugin("org.spire-math"  % "kind-projector" % "0.8.0" cross CrossVersion.binary),

      "com.chuusai"    %%% "shapeless"                 % version.shapeless,
      "io.github.soc"  %%% "scala-java-time"           % version.scalaJavaTime,
      "org.scalaz"     %%% "scalaz-core"               % version.scalaz,
      "org.scalaz"     %%% "scalaz-scalacheck-binding" % version.scalaz         % Test,
      "org.scalacheck" %%% "scalacheck"                % version.scalacheck     % Test,
      "org.scalatest"  %%% "scalatest"                 % version.scalatest      % Test
    )
  }

  lazy val coreJS = Def.settings(
    libraryDependencies ++= Seq(
      "org.scala-js"    %%% "scala-parser-combinators" % version.parserComb
    ),
    jsDependencies += RuntimeDOM % Test
  )

  lazy val coreJVM = Def.settings {
    libraryDependencies ++= Seq(
      "joda-time"               % "joda-time"                % version.jodaTime    % Optional,
      "org.joda"                % "joda-convert"             % version.jodaConvert % Optional,
      "org.scala-lang.modules" %% "scala-parser-combinators" % version.parserComb
    )
  }

  lazy val types = Def.settings {
    libraryDependencies ++= Seq(
      compilerPlugin("org.scalamacros" % "paradise"       % "2.1.0" cross CrossVersion.full),
      compilerPlugin("org.spire-math"  % "kind-projector" % "0.8.0" cross CrossVersion.binary),

      "org.scalaz"     %%% "scalaz-core"               % version.scalaz,
      "org.typelevel"  %%% "discipline"                % version.discipline % Test,
      "org.scalaz"     %%% "scalaz-scalacheck-binding" % version.scalaz     % Test,
      "org.scalacheck" %%% "scalacheck"                % version.scalacheck % Test
    )
  }

  lazy val testkit = Def.settings {
    libraryDependencies ++= Seq(
      compilerPlugin("org.scalamacros" % "paradise"       % "2.1.0" cross CrossVersion.full),
      compilerPlugin("org.spire-math"  % "kind-projector" % "0.8.0" cross CrossVersion.binary),

      "org.scalaz"     %%% "scalaz-core"               % version.scalaz,
      "org.typelevel"  %%% "discipline"                % version.discipline,
      "org.scalaz"     %%% "scalaz-scalacheck-binding" % version.scalaz,
      "org.scalacheck" %%% "scalacheck"                % version.scalacheck
    )
  }

}