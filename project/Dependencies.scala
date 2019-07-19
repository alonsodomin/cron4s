import sbt._
import Keys._

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType, _}
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.scalaJSVersion

object Dependencies {

  object version {
    val atto       = "0.6.5"
    val cats       = "2.0.0-M4"
    val shapeless  = "2.3.3"
    val scalacheck = "1.14.0"
    val scalatest  = "3.0.8"
    val discipline = "0.12.0-M3"
    val catalysts  = "0.8"
    val decline    = "0.7.0-M0"
    val circe      = "0.12.0-M4"
    val parserc    = "1.1.2"
    val doobie     = "0.8.0-M1"
    val contextual = "1.2.1"

    val jodaTime    = "2.10.2"
    val jodaConvert = "2.2.1"

    val momentjs      = "0.10.0"
    val scalaJavaTime = "2.0.0-RC3"
  }

  val macroParadise = compilerPlugin(
    "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
  )
  val kindProjector = compilerPlugin(
    "org.typelevel" % "kind-projector" % "0.10.3" cross CrossVersion.binary
  )

  lazy val core = Def.settings(
    libraryDependencies ++= Seq(
      "com.chuusai"            %%% "shapeless"                % version.shapeless,
      "org.typelevel"          %%% "cats-core"                % version.cats,
      "com.propensive"         %%% "contextual"               % version.contextual,      
    ),
    libraryDependencies += {
      val parsecVersion = CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, major)) if major < 12 => "1.1.1"
        case _ => version.parserc
      }
      "org.scala-lang.modules" %%% "scala-parser-combinators" % parsecVersion
    }
  )

  lazy val coreJS = Def.settings {
    libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % version.scalaJavaTime
  }

  lazy val coreJVM = Def.settings {
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % Provided
  }

  lazy val testkit = Def.settings {
    libraryDependencies ++= Seq(
      "org.typelevel"  %%% "cats-laws"            % version.cats,
      "org.typelevel"  %%% "cats-testkit"         % version.cats,
      "org.typelevel"  %%% "discipline-scalatest" % version.discipline,
      //"org.typelevel"  %%% "catalysts-platform"   % version.catalysts,
      "org.scalacheck" %%% "scalacheck"           % version.scalacheck,
      "org.scalatest"  %%% "scalatest"            % version.scalatest
    )
  }

  lazy val tests = Def.settings {
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % version.scalatest % Test
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

  lazy val bench = Def.settings {
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "atto-core" % version.atto,
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
    libraryDependencies += "ru.pavkin" %%% "scala-js-momentjs" % version.momentjs,
  )

  lazy val circe = Def.settings(
    libraryDependencies ++= {
      val circeVersion = CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n < 12 => "0.11.1"
        case _                      => version.circe
      }
      Seq(
        "io.circe" %%% "circe-core"    % circeVersion,
        "io.circe" %%% "circe-testing" % circeVersion % Test
      )
    }
  )

  lazy val decline = Def.settings(
    libraryDependencies += "com.monovore" %%% "decline" % version.decline
  )

  lazy val doobie = Def.settings(
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-core" % version.doobie,
      "org.tpolecat" %% "doobie-h2"   % version.doobie % Test
    )
  )

}
