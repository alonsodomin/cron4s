import sbt._
import Keys._

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType, _}
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.scalaJSVersion
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.npmDependencies

object Dependencies {
  object version {
    val atto = "0.9.0"

    object cats {
      val main      = "2.6.1"
      val scalatest = "2.1.5"
    }

    val shapeless  = "2.3.7"
    val discipline = "1.0.1"
    val decline    = "1.4.0"
    val circe      = "0.14.1"
    val parserc    = "2.1.1"
    val doobie     = "0.13.4"

    val jodaTime    = "2.10.10"
    val jodaConvert = "2.2.1"

    val momentjs      = "0.10.5"
    val momenttz      = "0.5.31"
    val scalaJavaTime = "2.3.0"
  }

  lazy val core = Def.settings(
    libraryDependencies ++= Seq(
      "com.chuusai"            %%% "shapeless"                % version.shapeless,
      "org.typelevel"          %%% "cats-core"                % version.cats.main,
      "org.scala-lang.modules" %%% "scala-parser-combinators" % version.parserc
    )
  )

  lazy val coreJS = Def.settings {
    libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % version.scalaJavaTime
  }

  lazy val coreJVM = Def.settings {
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % "1.0.0" % Provided
  }

  lazy val testkit = Def.settings {
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-laws"              % version.cats.main,
      "org.typelevel" %%% "cats-testkit"           % version.cats.main,
      "org.typelevel" %%% "cats-testkit-scalatest" % version.cats.scalatest,
      "org.typelevel" %%% "discipline-scalatest"   % version.discipline
    )
  }

  lazy val tests = Def.settings {
    libraryDependencies ++= Seq()
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
      "org.tpolecat" %% "atto-core" % version.atto
    )
  }

  // Dependencies of extension libraries

  lazy val joda = Def.settings {
    libraryDependencies ++= Seq(
      "joda-time" % "joda-time"    % version.jodaTime,
      "org.joda"  % "joda-convert" % version.jodaConvert
    )
  }

  private val momentjsNpmDeps = Seq(
    "moment-timezone" -> version.momenttz
  )
  lazy val momentjs = Def.settings(
    libraryDependencies += "ru.pavkin" %%% "scala-js-momentjs" % version.momentjs,
    Compile / npmDependencies ++= momentjsNpmDeps,
    Test / npmDependencies ++= momentjsNpmDeps
  )

  lazy val circe = Def.settings(
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core"    % version.circe,
      "io.circe" %%% "circe-testing" % version.circe % Test
    )
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
