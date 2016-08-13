
import com.typesafe.sbt.pgp.PgpKeys

import scala.xml.transform.{RewriteRule, RuleTransformer}

scalaVersion in ThisBuild := "2.11.8"

// TODO The parser combinators lib needs to support multiple Scala/ScalaJS versions to enable this
//crossScalaVersions in ThisBuild := Seq("2.10.4", "2.11.8", "2.12.0-M5")

val commonSettings = Def.settings(
  name := "cron4s",
  organization := "com.github.alonsodomin.cron4s",
  scalacOptions ++= Seq(
    "-language:postfixOps",
    "-feature",
    "-unchecked",
    "-deprecation",
    "-Xfuture",
    "-Xlint",
    "-Xfatal-warnings",
    "-Ywarn-dead-code",
    "-Yinline-warnings",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials"
  )
)

lazy val commonJsSettings = Seq(
  scalaJSStage in Test := FastOptStage,
  scalaJSUseRhino in Global := false,
  persistLauncher in Test := false,
  coverageExcludedFiles := ".*"
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  sonatypeProfileName := "com.github.alonsodomin",
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishTo := Some(
    if (isSnapshot.value) Opts.resolver.sonatypeSnapshots
    else Opts.resolver.sonatypeStaging
  ),
  // don't include scoverage as a dependency in the pom
  // see issue #980
  // this code was copied from https://github.com/mongodb/mongo-spark
  pomPostProcess := { (node: xml.Node) =>
    new RuleTransformer(
      new RewriteRule {
        override def transform(node: xml.Node): Seq[xml.Node] = node match {
          case e: xml.Elem
            if e.label == "dependency" && e.child.exists(child => child.label == "groupId" && child.text == "org.scoverage") => Nil
          case _ => Seq(node)

        }
      }).transform(node).head
  },
  pomExtra :=
    <url>https://github.com/alonsodomin/cron4s</url>
      <licenses>
        <license>
          <name>Apache License, Version 2.0</name>
          <url>https://www.apache.org/licenses/LICENSE-2.0</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:alonsodomin/cron4s.git</url>
        <connection>scm:git:git@github.com:alonsodomin/cron4s.git</connection>
      </scm>
      <developers>
        <developer>
          <id>alonsodomin</id>
          <name>Antonio Alonso Dominguez</name>
          <url>https://github.com/alonsodomin</url>
        </developer>
      </developers>
)

lazy val coverageSettings = Seq(
  coverageMinimum := 60,
  coverageFailOnMinimum := false,
  coverageHighlighting := true,
  coverageExcludedPackages := "cron4s\\.bench\\..*"
)

lazy val releaseSettings = {
  import ReleaseTransformations._

  Seq(
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts,
      setNextVersion,
      commitNextVersion,
      ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
      pushChanges
    )
  )
}

lazy val cron4s = (project in file(".")).
  settings(commonSettings).
  settings(noPublishSettings).
  settings(releaseSettings).
  aggregate(cron4sJS, cron4sJVM)

lazy val cron4sJS = (project in file(".js")).
  settings(
    name := "cron4s",
    moduleName := "cron4s"
  ).
  settings(commonSettings: _*).
  settings(commonJsSettings: _*).
  settings(publishSettings).
  enablePlugins(ScalaJSPlugin).
  aggregate(typesJS, coreJS).
  dependsOn(typesJS, coreJS)

lazy val cron4sJVM = (project in file(".jvm")).
  settings(
    name := "cron4s",
    moduleName := "cron4s"
  ).
  settings(commonSettings: _*).
  settings(publishSettings).
  aggregate(typesJVM, coreJVM).
  dependsOn(typesJVM, coreJVM)

lazy val types = (crossProject.crossType(CrossType.Pure) in file("types")).
  settings(
    name := "types",
    moduleName := "cron4s-types"
  ).
  settings(commonSettings: _*).
  settings(commonJsSettings: _*).
  settings(publishSettings: _*).
  settings(Dependencies.types: _*)

lazy val typesJS = types.js
lazy val typesJVM = types.jvm

lazy val core = (crossProject in file("core")).
  settings(
    name := "core",
    moduleName := "cron4s-core"
  ).
  settings(commonSettings: _*).
  settings(commonJsSettings: _*).
  settings(publishSettings: _*).
  settings(Dependencies.core: _*).
  jvmSettings(Dependencies.coreJVM: _*).
  jsSettings(Dependencies.coreJS: _*).
  dependsOn(types)

lazy val coreJS = core.js
lazy val coreJVM = core.jvm

lazy val bench = (project in file("bench")).
  settings(name := "bench").
  settings(commonSettings).
  settings(noPublishSettings).
  enablePlugins(JmhPlugin).
  dependsOn(coreJVM)
