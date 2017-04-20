
import com.typesafe.sbt.pgp.PgpKeys

import com.typesafe.tools.mima.plugin.MimaKeys.mimaPreviousArtifacts
import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings

import scala.xml.transform.{RewriteRule, RuleTransformer}

scalaVersion in ThisBuild := "2.12.2"

crossScalaVersions in ThisBuild := Seq(scalaVersion.value, "2.11.11")

lazy val botBuild = settingKey[Boolean]("Build by TravisCI instead of local dev environment")

lazy val consoleImports = settingKey[Seq[String]]("Base imports in the console")

val commonSettings = Def.settings(
  name := "cron4s",
  organization := "com.github.alonsodomin.cron4s",
  description := "CRON expression parser for Scala",
  scmInfo := Some(ScmInfo(
    url("https://github.com/alonsodomin/cron4s"),
    "scm:git:git@github.com:alonsodomin/cron4s.git"
  )),
  scalacOptions ++= Seq(
    "-language:postfixOps",
    "-feature",
    "-unchecked",
    "-deprecation",
    "-Xfuture",
    "-Xlint",
    "-Xfatal-warnings",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials"
  ),
  botBuild := scala.sys.env.get("TRAVIS").isDefined,
  parallelExecution in Test := false,
  consoleImports := Seq("cron4s._"),
  initialCommands in console := consoleImports.value.map(s => s"import $s").mkString("\n") + "\n"
) ++ Licensing.settings

lazy val commonJvmSettings = Seq(
  fork in Test := false
)

lazy val commonJsSettings = Seq(
  scalaJSStage in Test := FastOptStage,
  requiresDOM := false,
  // batch mode decreases the amount of memory needed to compile scala.js code
  scalaJSOptimizerOptions := scalaJSOptimizerOptions.value.withBatchMode(botBuild.value),
  scalacOptions += {
    val tagOrHash = {
      if (isSnapshot.value) sys.process.Process("git rev-parse HEAD").lines_!.head
      else version.value
    }
    val a = (baseDirectory in LocalRootProject).value.toURI.toString
    val g = "https://raw.githubusercontent.com/alonsodomin/cron4s/" + tagOrHash
    s"-P:scalajs:mapSourceURI:$a->$g/"
  },
  jsEnv := PhantomJSEnv().value
)

lazy val consoleSettings = Seq(
  consoleImports ++= Seq("java.time._", "cron4s.lib.javatime._")
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

lazy val publishSettings = Seq(
  homepage := Some(url("https://github.com/alonsodomin/cron4s")),
  licenses := Seq("Apache 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
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
  coverageExcludedPackages := "cron4s\\.bench\\..*"
)

def mimaSettings(module: String): Seq[Setting[_]] = mimaDefaultSettings ++ Seq(
  mimaPreviousArtifacts := Set("com.github.alonsodomin.cron4s" %% s"cron4s-${module}" % "0.3.2")
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
    "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath,
    "-diagrams"
  )
)

lazy val releaseSettings = {
  import ReleaseTransformations._

  val sonatypeReleaseAll = ReleaseStep(
    action = Command.process("sonatypeReleaseAll", _),
    enableCrossBuild = true
  )

  Seq(
    sonatypeProfileName := "com.github.alonsodomin",
    releaseCrossBuild := true,
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
  aggregate(coreJS, momentjs, testkitJS, testsJS).
  dependsOn(coreJS, momentjs, testkitJS, testsJS  % Test)

lazy val cron4sJVM = (project in file(".jvm")).
  settings(
    name := "cron4s",
    moduleName := "cron4s"
  ).
  settings(commonSettings).
  settings(commonJvmSettings).
  settings(consoleSettings).
  settings(publishSettings).
  aggregate(coreJVM, joda, testkitJVM, testsJVM).
  dependsOn(coreJVM, joda, testkitJVM, testsJVM % Test)

lazy val docs = project.
  enablePlugins(MicrositesPlugin, ScalaUnidocPlugin, GhpagesPlugin).
  settings(moduleName := "cron4s-docs").
  settings(commonSettings).
  settings(noPublishSettings).
  settings(docSettings).
  dependsOn(cron4sJVM)

lazy val core = (crossProject in file("core")).
  enablePlugins(AutomateHeaderPlugin).
  settings(
    name := "core",
    moduleName := "cron4s-core"
  ).
  settings(commonSettings).
  settings(publishSettings).
  settings(Dependencies.core).
  jsSettings(commonJsSettings).
  jsSettings(Dependencies.coreJS).
  jvmSettings(commonJvmSettings).
  jvmSettings(consoleSettings).
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
  settings(commonSettings).
  settings(publishSettings).
  settings(Dependencies.testkit).
  jsSettings(commonJsSettings).
  jvmSettings(commonJvmSettings).
  jvmSettings(consoleSettings).
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
  settings(commonSettings).
  settings(noPublishSettings).
  settings(Dependencies.tests).
  jsSettings(commonJsSettings).
  jsSettings(Dependencies.testsJS).
  jvmSettings(commonJvmSettings).
  jvmSettings(Dependencies.testsJVM).
  dependsOn(testkit % Test)

lazy val testsJS = tests.js
lazy val testsJVM = tests.jvm

lazy val bench = (project in file("bench")).
  enablePlugins(AutomateHeaderPlugin).
  settings(
    name := "bench",
    moduleName := "cron4s-bench"
  ).
  settings(commonSettings).
  settings(noPublishSettings).
  enablePlugins(JmhPlugin).
  dependsOn(coreJVM)

// DateTime library extensions

lazy val joda = (project in file("time-lib/joda")).
  enablePlugins(AutomateHeaderPlugin).
  settings(
    name := "joda",
    moduleName := "cron4s-joda",
    consoleImports ++= Seq("org.joda.time._", "cron4s.lib.joda._")
  ).
  settings(commonSettings).
  settings(commonJvmSettings).
  settings(publishSettings).
  settings(Dependencies.joda).
  dependsOn(coreJVM, testkitJVM % Test)

lazy val momentjs = (project in file("time-lib/momentjs")).
  enablePlugins(AutomateHeaderPlugin, ScalaJSPlugin).
  settings(commonSettings).
  settings(commonJsSettings).
  settings(publishSettings).
  settings(
    name := "momentjs",
    moduleName := "cron4s-momentjs"
  ).
  settings(Dependencies.momentjs).
  dependsOn(coreJS, testkitJS % Test)

// Utility command aliases

addCommandAlias("testJVM", "cron4sJVM/test")
addCommandAlias("testJS", "cron4sJS/test")
addCommandAlias("validateJVM", ";testJVM;makeMicrosite")
addCommandAlias("validateJS", "testJS")
