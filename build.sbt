
import com.typesafe.sbt.pgp.PgpKeys
import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
import com.typesafe.tools.mima.plugin.MimaKeys.mimaPreviousArtifacts
import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import sbtunidoc.Plugin.UnidocKeys._

import scala.xml.transform.{RewriteRule, RuleTransformer}

scalaVersion in ThisBuild := "2.12.1"

crossScalaVersions in ThisBuild := Seq(scalaVersion.value, "2.11.8")

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
    //"-Ywarn-dead-code",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials"
  ),
  scmInfo := Some(ScmInfo(
    url("https://github.com/alonsodomin/cron4s"),
    "scm:git:git@github.com:alonsodomin/cron4s.git"
  )),
  parallelExecution in Test := false,
  fork in Test := true
) ++ Licensing.settings

lazy val commonJsSettings = Seq(
  scalaJSStage in Test := FastOptStage,
  persistLauncher in Test := false,
  requiresDOM := false
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
  autoAPIMappings := true,
  apiURL := Some(url("https://alonsodomin.github.io/cron4s/api/")),
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
  coverageMinimum := 80,
  coverageFailOnMinimum := true,
  coverageHighlighting := true,
  coverageExcludedPackages := "cron4s\\.bench\\..*;cron4s\\.testkit\\..*"
)

def mimaSettings(module: String): Seq[Setting[_]] = mimaDefaultSettings ++ Seq(
  mimaPreviousArtifacts := Set("com.github.alonsodomin.cron4s" %% s"cron4s-${module}" % "0.2.1")
)

lazy val docsMappingsAPIDir = settingKey[String]("Name of subdirectory in site target directory for api docs")

lazy val docSettings = Seq(
  micrositeName := "Cron4s",
  micrositeDescription := "Scala CRON Parser",
  micrositeHighlightTheme := "atom-one-light",
  micrositeAuthor := "Antonio Alonso Dominguez",
  micrositeGithubOwner := "alonsodomin",
  micrositeGithubRepo := "cron4s",
  micrositeHomepage := "https://alonsodomin.github.io/cron4s",
  micrositeBaseUrl := "/cron4s",
  micrositeDocumentationUrl := "docs",
  fork in tut := true,
  fork in (ScalaUnidoc, unidoc) := true,
  autoAPIMappings := true,
  docsMappingsAPIDir := "api",
  addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), docsMappingsAPIDir),
  ghpagesNoJekyll := false,
  git.remoteRepo := "https://github.com/alonsodomin/cron4s.git",
  unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(coreJVM),
  scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
    "-Xfatal-warnings",
    "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
    "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath
    //"-diagrams"
  )
)

lazy val releaseSettings = {
  import ReleaseTransformations._

  val sonatypeReleaseAll = ReleaseStep(action = Command.process("sonatypeReleaseAll", _))

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
      sonatypeReleaseAll,
      pushChanges
    )
  )
}

lazy val cron4s = (project in file(".")).
  settings(commonSettings).
  settings(noPublishSettings).
  settings(releaseSettings).
  aggregate(cron4sJS, cron4sJVM, docs, bench)

lazy val cron4sJS = (project in file(".js")).
  settings(
    name := "cron4s",
    moduleName := "cron4s"
  ).
  settings(commonSettings: _*).
  settings(commonJsSettings: _*).
  settings(publishSettings).
  enablePlugins(ScalaJSPlugin).
  aggregate(coreJS, testkitJS, testsJS).
  dependsOn(coreJS, testkitJS, testsJS)

lazy val cron4sJVM = (project in file(".jvm")).
  settings(
    name := "cron4s",
    moduleName := "cron4s"
  ).
  settings(commonSettings: _*).
  settings(publishSettings).
  aggregate(coreJVM, testkitJVM, testsJVM).
  dependsOn(coreJVM, testkitJVM, testsJVM)

lazy val docs = project.
  enablePlugins(MicrositesPlugin).
  settings(moduleName := "cron4s-docs").
  settings(commonSettings).
  settings(noPublishSettings).
  settings(ghpages.settings).
  settings(unidocSettings).
  settings(docSettings).
  dependsOn(cron4sJVM)

lazy val core = (crossProject in file("core")).
  enablePlugins(AutomateHeaderPlugin).
  settings(
    name := "core",
    moduleName := "cron4s-core"
  ).
  settings(commonSettings: _*).
  settings(commonJsSettings: _*).
  settings(publishSettings: _*).
  settings(Dependencies.core: _*).
  jvmSettings(Dependencies.coreJVM: _*).
  jvmSettings(mimaSettings("core"): _*)

lazy val coreJS = core.js
lazy val coreJVM = core.jvm

lazy val testkit = (crossProject.crossType(CrossType.Pure) in file("testkit")).
  enablePlugins(AutomateHeaderPlugin).
  settings(
    name := "testkit",
    moduleName := "cron4s-testkit"
  ).
  settings(commonSettings: _*).
  settings(commonJsSettings: _*).
  settings(publishSettings: _*).
  settings(Dependencies.testkit: _*).
  jvmSettings(mimaSettings("testkit"): _*).
  dependsOn(core)

lazy val testkitJS = testkit.js
lazy val testkitJVM = testkit.jvm

lazy val tests = (crossProject in file("tests")).
  enablePlugins(AutomateHeaderPlugin).
  settings(
    name := "tests",
    moduleName := "cron4s-tests"
  ).
  settings(commonSettings: _*).
  settings(commonJsSettings: _*).
  settings(noPublishSettings: _*).
  settings(Dependencies.tests: _*).
  jvmSettings(Dependencies.testsJVM: _*).
  dependsOn(testkit % Test)

lazy val testsJS = tests.js
lazy val testsJVM = tests.jvm

lazy val bench = (project in file("bench")).
  enablePlugins(AutomateHeaderPlugin).
  settings(name := "bench").
  settings(commonSettings).
  settings(noPublishSettings).
  enablePlugins(JmhPlugin).
  dependsOn(coreJVM)

// Utility command aliases

addCommandAlias("testJVM", "cron4sJVM/test")
addCommandAlias("testJS", "cron4sJS/test")
addCommandAlias("validateJVM", ";testJVM;makeMicrosite")
addCommandAlias("validateJS", "testJS")