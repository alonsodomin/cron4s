import sbt._
import Keys._

import sbtcrossproject.CrossPlugin.autoImport._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import scalajscrossproject.ScalaJSCrossPlugin.autoImport.{toScalaJSGroupID => _, _}
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.scalaJSVersion

object Dependencies {

  object version {
    val cats        = "1.1.0"
    val shapeless   = "2.3.3"
    val fastparse   = "1.0.0"
    val scalacheck  = "1.14.0"
    val scalatest   = "3.0.5"
    val discipline  = "0.9.0"
    val catalysts   = "0.6"

    val jodaTime      = "2.10"
    val jodaConvert   = "1.9.2"
    val momentjs      = "0.8.1"
    val scalaJavaTime = "2.0.0-M13"
  }

  val macroParadise = compilerPlugin("org.scalamacros" % "paradise"       % "2.1.1" cross CrossVersion.full)
  val kindProjector = compilerPlugin("org.spire-math"  % "kind-projector" % "0.9.8" cross CrossVersion.binary)
  lazy val compilerPlugins = Seq(macroParadise, kindProjector)

  lazy val core = Def.settings {
    libraryDependencies ++= compilerPlugins ++ Seq(
      "com.chuusai"   %%% "shapeless" % version.shapeless,
      "org.typelevel" %%% "cats-core" % version.cats,
      "com.lihaoyi"   %%% "fastparse" % version.fastparse
    )
  }

  lazy val coreJS = Def.settings {
    libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % version.scalaJavaTime
  }

  lazy val coreJVM = Def.settings {
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % Provided
  }

  lazy val testkit = Def.settings {
    libraryDependencies ++= compilerPlugins ++ Seq(
      "org.typelevel"  %%% "cats-laws"          % version.cats,
      "org.typelevel"  %%% "cats-testkit"       % version.cats,
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

  // Dependencies of extension libraries

  lazy val joda = Def.settings {
    libraryDependencies ++= Seq(
      "joda-time" % "joda-time"    % version.jodaTime,
      "org.joda"  % "joda-convert" % version.jodaConvert
    )
  }

  lazy val momentjs = Def.settings(
    libraryDependencies += "ru.pavkin" %%% "scala-js-momentjs" % version.momentjs
  )

}
