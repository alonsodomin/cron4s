
import com.typesafe.sbt.pgp.PgpKeys
import com.typesafe.sbt.SbtGhPages.GhPagesKeys._

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
  ),
  scmInfo := Some(ScmInfo(url("https://github.com/alonsodomin/cron4s"), "scm:git:git@github.com:alonsodomin/cron4s.git"))
)

lazy val commonJsSettings = Seq(
  scalaJSStage in Test := FastOptStage,
  scalaJSUseRhino in Global := false,
  persistLauncher in Test := false
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
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

lazy val docSettings = Seq(
  micrositeName := "Cron4s",
  micrositeDescription := "Scala CRON Expressions",
  micrositeHighlightTheme := "atom-one-light",
  micrositeGithubOwner := "alonsodomin",
  micrositeGithubRepo := "cron4s",
  micrositeHomepage := "http://alonsodomin.github.io/cron4s",
  micrositeDocumentationUrl := "docs",
  fork in tut := true,
  ghpagesNoJekyll := false,
  git.remoteRepo := "https://github.com/alonsodomin/cron4s.git"
)

lazy val releaseSettings = {
  import ReleaseTransformations._

  Seq(
    sonatypeProfileName := "com.github.alonsodomin",
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
  aggregate(cron4sJS, cron4sJVM, docs)

lazy val cron4sJS = (project in file(".js")).
  settings(
    name := "cron4s",
    moduleName := "cron4s"
  ).
  settings(commonSettings: _*).
  settings(commonJsSettings: _*).
  settings(publishSettings).
  enablePlugins(ScalaJSPlugin).
  aggregate(typesJS, testkitJS, coreJS, testsJS).
  dependsOn(typesJS, testkitJS, coreJS, testsJS)

lazy val cron4sJVM = (project in file(".jvm")).
  settings(
    name := "cron4s",
    moduleName := "cron4s"
  ).
  settings(commonSettings: _*).
  settings(publishSettings).
  aggregate(typesJVM, testkitJVM, coreJVM, testsJVM).
  dependsOn(typesJVM, testkitJVM, coreJVM, testsJVM)

lazy val docs = project.
  enablePlugins(MicrositesPlugin).
  settings(moduleName := "cron4s-docs").
  settings(commonSettings).
  settings(noPublishSettings).
  settings(ghpages.settings).
  settings(docSettings).
  dependsOn(coreJVM)

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

lazy val testkit = (crossProject.crossType(CrossType.Pure) in file("testkit")).
  settings(
    name := "testkit",
    moduleName := "cron4s-testkit"
  ).
  settings(commonSettings: _*).
  settings(commonJsSettings: _*).
  settings(publishSettings: _*).
  settings(Dependencies.testkit: _*).
  dependsOn(core)

lazy val testkitJS = testkit.js
lazy val testkitJVM = testkit.jvm

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

lazy val tests = (crossProject in file("tests")).
  settings(
    name := "tests",
    moduleName := "cron4s-tests"
  ).
  settings(commonSettings: _*).
  settings(commonJsSettings: _*).
  settings(noPublishSettings: _*).
  settings(Dependencies.tests: _*).
  jvmSettings(Dependencies.testsJVM: _*).
  jsSettings(Dependencies.testsJS: _*).
  dependsOn(testkit % Test)

lazy val testsJS = tests.js
lazy val testsJVM = tests.jvm

lazy val bench = (project in file("bench")).
  settings(name := "bench").
  settings(commonSettings).
  settings(noPublishSettings).
  enablePlugins(JmhPlugin).
  dependsOn(coreJVM)
