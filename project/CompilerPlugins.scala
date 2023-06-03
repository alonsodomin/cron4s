import sbt._
import Keys._

object CompilerPlugins {
  val macroParadise = compilerPlugin(
    "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
  )
  val kindProjector = compilerPlugin(
    "org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full
  )
  val betterMonadicFor = compilerPlugin(
    "com.olegpy" %% "better-monadic-for" % "0.3.1"
  )

  lazy val All = Def.settings(
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n <= 12 => Seq(macroParadise, betterMonadicFor, kindProjector)
        case Some((2, 13))           => Seq(betterMonadicFor, kindProjector)
        case _                       => Nil
      }
    }
  )
}
