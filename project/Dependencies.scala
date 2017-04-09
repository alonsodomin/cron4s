import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._
import Keys._

object Dependencies {

  object version {
    val cats        = "0.9.0"
    val shapeless   = "2.3.2"

    val momentjs    = "0.1.5"
    val jodaTime    = "2.9.7"
    val jodaConvert = "1.8.1"
    val fastparse   = "0.4.2"

    val scalacheck  = "1.13.5"
    val scalatest   = "3.0.1"
    val discipline  = "0.7.3"
    val catalysts   = "0.0.5"

    val scalaJavaTime = "2.0.0-M9"
  }

  val macroParadise = compilerPlugin("org.scalamacros" % "paradise"       % "2.1.0" cross CrossVersion.full)
  val kindProjector = compilerPlugin("org.spire-math"  % "kind-projector" % "0.9.3" cross CrossVersion.binary)
  lazy val compilerPlugins = Seq(macroParadise, kindProjector)

  lazy val core = Def.settings {
    libraryDependencies ++= compilerPlugins ++ Seq(
      "com.chuusai"   %%% "shapeless" % version.shapeless,
      "org.typelevel" %%% "cats"      % version.cats,
      "com.lihaoyi"   %%% "fastparse" % version.fastparse
    )
  }

  lazy val coreJS = Def.settings {
    libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % version.scalaJavaTime
  }

  lazy val coreJVM = Def.settings {
    libraryDependencies ++= Seq(
      "joda-time" % "joda-time"    % version.jodaTime    % Optional,
      "org.joda"  % "joda-convert" % version.jodaConvert % Optional
    )
  }

  lazy val testkit = Def.settings {
    libraryDependencies ++= compilerPlugins ++ Seq(
      "org.typelevel"  %%% "cats"               % version.cats,
      "org.typelevel"  %%% "cats-laws"          % version.cats,
      "org.typelevel"  %%% "discipline"         % version.discipline,
      "org.typelevel"  %%% "catalysts-platform" % version.catalysts,
      "org.scalacheck" %%% "scalacheck"         % version.scalacheck,
      "org.scalatest"  %%% "scalatest"          % version.scalatest
    )
  }

  lazy val tests = Def.settings {
    libraryDependencies ++= compilerPlugins ++ Seq(
      "org.scalatest"     %%% "scalatest"       % version.scalatest % Test
    )
  }

  lazy val testsJS = Def.settings {
    libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % version.scalaJavaTime
  }

  lazy val testsJVM = Def.settings {
    libraryDependencies ++= Seq(
      "joda-time" % "joda-time"    % version.jodaTime,
      "org.joda"  % "joda-convert" % version.jodaConvert
    )
  }

}