import sbt._
import Keys._

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType, _}
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.scalaJSVersion
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.npmDependencies

object Dependencies {
  object version {
    val atto = "0.8.0"

    object cats {
      val main      = "2.1.1"
      val scalatest = "1.0.1"
    }

    val shapeless  = "2.3.3"
    val discipline = "1.0.1"
    val decline    = "1.3.0"
    val circe      = "0.13.0"
    val parserc    = "1.1.2"
    val doobie     = "0.9.2"

    val jodaTime    = "2.10.7"
    val jodaConvert = "2.2.1"

    val momentjs      = "0.10.4"
    val momenttz      = "0.5.28"
    val scalaJavaTime = "2.0.0"

    val scalaJSStubs = "1.0.0"
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
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % version.scalaJSStubs % Provided
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
    npmDependencies in Compile ++= momentjsNpmDeps,
    npmDependencies in Test ++= momentjsNpmDeps
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
