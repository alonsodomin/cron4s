import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._
import Keys._

object Dependencies {

  object version {
    val simulacrum = "0.7.0"
    val cats       = "0.6.0"
    val shapeless  = "2.3.1"
    val scalacheck = "1.12.5"
    val momentjs   = "0.1.5"
    val jodaTime   = "2.9.4"
    val parserComb = "1.0.2"
  }

  lazy val core = Def.settings {
    libraryDependencies ++= Seq(
      compilerPlugin("org.scalamacros" % "paradise"       % "2.1.0" cross CrossVersion.full),
      compilerPlugin("org.spire-math"  % "kind-projector" % "0.8.0" cross CrossVersion.binary),

      "com.chuusai"          %%% "shapeless"  % version.shapeless,
      "org.scalacheck"       %%% "scalacheck" % version.scalacheck % Test
    )
  }

  lazy val coreJS = Def.settings {
    libraryDependencies ++= Seq(
      "io.github.widok" %%% "scala-js-momentjs"        % version.momentjs % Optional,
      "org.scala-js"    %%% "scala-parser-combinators" % version.parserComb
    )
  }

  lazy val coreJVM = Def.settings {
    libraryDependencies ++= Seq(
      "joda-time"               % "joda-time"                % version.jodaTime % Optional,
      "org.scala-lang.modules" %% "scala-parser-combinators" % version.parserComb
    )
  }

  lazy val cats = Def.settings {
    libraryDependencies ++= Seq(
      compilerPlugin("org.scalamacros" % "paradise"       % "2.1.0" cross CrossVersion.full),
      compilerPlugin("org.spire-math"  % "kind-projector" % "0.8.0" cross CrossVersion.binary),

      "com.github.mpilquist" %%% "simulacrum" % version.simulacrum,
      "org.typelevel"        %%% "cats"       % version.cats
    )
  }

}