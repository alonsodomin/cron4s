import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}
import com.typesafe.sbt.pgp.PgpKeys
import com.typesafe.tools.mima.core._
import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings

import scala.xml.transform.{RewriteRule, RuleTransformer}

lazy val consoleImports =
  settingKey[Seq[String]]("Base imports in the console")

lazy val unusedWarning = Seq(
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 11)) =>
        Seq("-Ywarn-unused-import")
      case Some((2, n)) if n >= 12 =>
        Seq("-Xlint:-unused,_")
    }
  },
  scalacOptions in (Compile, console) := scalacOptions.value.filterNot(
    Set("-Ywarn-unused-import", "-Xlint:-unused,_", "-Xfatal-warnings")
  ),
  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
  scalacOptions in Tut := (scalacOptions in (Compile, console)).value
)

val commonSettings = Def.settings(
  name := "cron4s",
  organization := "com.github.alonsodomin.cron4s",
  organizationName := "Antonio Alonso Dominguez",
  description := "CRON expression parser for Scala",
  startYear := Some(2017),
  homepage := Some(url("https://github.com/alonsodomin/cron4s")),
  licenses += ("Apache-2.0", url(
    "https://www.apache.org/licenses/LICENSE-2.0.txt")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/alonsodomin/cron4s"),
      "scm:git:git@github.com:alonsodomin/cron4s.git"
    )
  ),
  developers += Developer(
    "alonsodomin",
    "A. Alonso Dominguez",
    "",
    url("https://github.com/alonsodomin")
  ),
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-feature",
    "-unchecked",
    "-deprecation",
    "-Xfuture",
    "-Xfatal-warnings",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials",
    "-Ypartial-unification"
  ),
  parallelExecution in Test := false,
  consoleImports := Seq("cron4s._"),
  initialCommands in console := consoleImports.value
    .map(s => s"import $s")
    .mkString("\n"),
  scalafmtOnCompile := true
) ++ unusedWarning

lazy val commonJvmSettings = Seq(
  fork in Test := false
)

lazy val commonJsSettings = Seq(
  scalaJSStage in Global := FastOptStage,
  // batch mode decreases the amount of memory needed to compile scala.js code
  scalaJSOptimizerOptions := scalaJSOptimizerOptions.value.withBatchMode(
    isTravisBuild.value),
  scalacOptions += {
    val tagOrHash = {
      if (isSnapshot.value)
        sys.process.Process("git rev-parse HEAD").lineStream_!.head
      else version.value
    }
    val a = (baseDirectory in LocalRootProject).value.toURI.toString
    val g = "https://raw.githubusercontent.com/alonsodomin/cron4s/" + tagOrHash
    s"-P:scalajs:mapSourceURI:$a->$g/"
  },
  parallelExecution := false,
  jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
)

lazy val consoleSettings = Seq(
  consoleImports ++= Seq("java.time._", "cron4s.lib.javatime._")
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
    new RuleTransformer(new RewriteRule {
      override def transform(node: xml.Node): Seq[xml.Node] = node match {
        case e: xml.Elem
            if e.label == "dependency" && e.child.exists(child =>
              child.label == "groupId" && child.text == "org.scoverage") =>
          Nil
        case _ => Seq(node)

      }
    }).transform(node).head
  }
)

lazy val noPublishSettings = publishSettings ++ Seq(
  skip in publish := true,
  publishArtifact := false
)

lazy val coverageSettings = Seq(
  coverageMinimum := 80,
  coverageFailOnMinimum := true,
  coverageHighlighting := true,
  coverageExcludedPackages := "cron4s\\.bench\\..*"
)

def mimaSettings(module: String, versions: Set[String]): Seq[Setting[_]] = mimaDefaultSettings ++ Seq(
  mimaPreviousArtifacts := versions.map(organization.value %% s"cron4s-$module" % _),
  mimaBackwardIssueFilters += "0.4.5" -> Seq(
    ProblemFilters.exclude[IncompatibleMethTypeProblem]("cron4s.Error.this"),
    ProblemFilters.exclude[MissingClassProblem]("cron4s.parser.package$"),
    ProblemFilters.exclude[MissingClassProblem]("cron4s.parser.package"),
    ProblemFilters.exclude[DirectMissingMethodProblem]("cron4s.expr.SeveralNode.apply"),
    ProblemFilters.exclude[DirectMissingMethodProblem]("cron4s.expr.SeveralNode.this"),
    ProblemFilters.exclude[DirectMissingMethodProblem]("cron4s.expr.FieldNodeWithAny.fieldNodeInstance"),
    ProblemFilters.exclude[ReversedMissingMethodProblem]("cron4s.expr.NodeConversions.field2FieldWithAny"),
    ProblemFilters.exclude[ReversedMissingMethodProblem]("cron4s.expr.NodeConversions.enumerable2Field"),
    ProblemFilters.exclude[ReversedMissingMethodProblem]("cron4s.expr.NodeConversions.divisible2Field"),
  )
)

lazy val docsMappingsAPIDir = settingKey[String](
  "Name of subdirectory in site target directory for api docs")

lazy val docSettings = Seq(
  micrositeName := "Cron4s",
  micrositeDescription := "Scala CRON Parser",
  micrositeHighlightTheme := "atom-one-light",
  micrositeAuthor := "Antonio Alonso Dominguez",
  micrositeGithubOwner := "alonsodomin",
  micrositeGithubRepo := "cron4s",
  micrositeGitterChannel := true,
  micrositeHomepage := "https://alonsodomin.github.io/cron4s",
  micrositeBaseUrl := "/cron4s",
  micrositeDocumentationUrl := "docs",
  fork in tut := true,
  fork in (ScalaUnidoc, unidoc) := true,
  autoAPIMappings := true,
  docsMappingsAPIDir := "api",
  addMappingsToSiteDir(
    mappings in (ScalaUnidoc, packageDoc),
    docsMappingsAPIDir
  ),
  ghpagesNoJekyll := false,
  git.remoteRepo := "https://github.com/alonsodomin/cron4s.git",
  unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(coreJVM),
  scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
    "-Xfatal-warnings",
    "-doc-source-url",
    scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
    "-sourcepath",
    baseDirectory.in(LocalRootProject).value.getAbsolutePath,
    "-diagrams"
  )
)

lazy val releaseSettings = {
  import ReleaseTransformations._

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
      releaseStepCommand("sonatypeReleaseAll"),
      pushChanges
    )
  )
}

lazy val cron4s = (project in file("."))
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(releaseSettings)
  .aggregate(cron4sJS, cron4sJVM, docs, bench)

lazy val cron4sJS = (project in file(".js"))
  .settings(
    name := "cron4s",
    moduleName := "cron4s"
  )
  .settings(commonSettings: _*)
  .settings(commonJsSettings: _*)
  .settings(publishSettings)
  .enablePlugins(ScalaJSPlugin)
  .aggregate(coreJS, momentjs, circeJS, declineJS, testkitJS, testsJS)
  .dependsOn(coreJS, momentjs, circeJS, declineJS, testkitJS, testsJS % Test)

lazy val cron4sJVM = (project in file(".jvm"))
  .settings(
    name := "cron4s",
    moduleName := "cron4s"
  )
  .settings(commonSettings)
  .settings(commonJvmSettings)
  .settings(consoleSettings)
  .settings(publishSettings)
  .aggregate(coreJVM, joda, circeJVM, declineJVM, testkitJVM, testsJVM)
  .dependsOn(coreJVM, joda, circeJVM, declineJVM, testkitJVM, testsJVM % Test)

lazy val docs = project
  .enablePlugins(MicrositesPlugin, ScalaUnidocPlugin, GhpagesPlugin)
  .settings(moduleName := "cron4s-docs")
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(docSettings)
  .dependsOn(cron4sJVM)

lazy val core = (crossProject(JSPlatform, JVMPlatform) in file("modules/core"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin)
  .settings(
    name := "core",
    moduleName := "cron4s-core"
  )
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(Dependencies.core)
  .jsSettings(commonJsSettings)
  .jsSettings(Dependencies.coreJS)
  .jvmSettings(commonJvmSettings)
  .jvmSettings(consoleSettings)
  .jvmSettings(Dependencies.coreJVM)
  .jvmSettings(mimaSettings("core", Set("0.4.5", "0.5.0")))

lazy val coreJS = core.js
lazy val coreJVM = core.jvm

lazy val testkit =
  (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file(
    "modules/testkit"))
    .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin)
    .settings(
      name := "testkit",
      moduleName := "cron4s-testkit"
    )
    .settings(commonSettings)
    .settings(publishSettings)
    .settings(Dependencies.testkit)
    .jsSettings(commonJsSettings)
    .jvmSettings(commonJvmSettings)
    .jvmSettings(consoleSettings)
    .jvmSettings(mimaSettings("testkit", Set("0.4.5", "0.5.0")))
    .dependsOn(core)

lazy val testkitJS = testkit.js
lazy val testkitJVM = testkit.jvm

lazy val tests = (crossProject(JSPlatform, JVMPlatform) in file("tests"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin)
  .settings(
    name := "tests",
    moduleName := "cron4s-tests"
  )
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(Dependencies.tests)
  .jsSettings(commonJsSettings)
  .jsSettings(Dependencies.testsJS)
  .jvmSettings(commonJvmSettings)
  .jvmSettings(Dependencies.testsJVM)
  .dependsOn(testkit % Test)

lazy val testsJS = tests.js
lazy val testsJVM = tests.jvm

lazy val bench = (project in file("bench"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin)
  .settings(
    name := "bench",
    moduleName := "cron4s-bench"
  )
  .settings(commonSettings)
  .settings(noPublishSettings)
  .enablePlugins(JmhPlugin)
  .dependsOn(coreJVM)

// DateTime library extensions

lazy val joda = (project in file("modules/joda"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin)
  .settings(
    name := "joda",
    moduleName := "cron4s-joda",
    consoleImports ++= Seq("org.joda.time._", "cron4s.lib.joda._")
  )
  .settings(commonSettings)
  .settings(commonJvmSettings)
  .settings(publishSettings)
  .settings(Dependencies.joda)
  .settings(mimaSettings("joda", Set("0.4.5", "0.5.0")))
  .dependsOn(coreJVM, testkitJVM % Test)

lazy val momentjs = (project in file("modules/momentjs"))
  .enablePlugins(AutomateHeaderPlugin, ScalaJSPlugin, ScalafmtPlugin)
  .settings(commonSettings)
  .settings(commonJsSettings)
  .settings(publishSettings)
  .settings(
    name := "momentjs",
    moduleName := "cron4s-momentjs"
  )
  .settings(Dependencies.momentjs)
  .dependsOn(coreJS, testkitJS % Test)

// Extension modules

lazy val circe = (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("modules/circe"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin)
  .settings(
    name := "circe",
    moduleName := "cron4s-circe"
  )
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(Dependencies.circe)
  .settings(mimaSettings("circe", Set("0.5.0")))
  .dependsOn(core, testkit % Test)

lazy val circeJVM = circe.jvm
lazy val circeJS  = circe.js

lazy val decline = (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("modules/decline"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin)
  .settings(
    name := "decline",
    moduleName := "cron4s-decline"
  )
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(Dependencies.decline)
  .settings(mimaSettings("decline", Set("0.5.0")))
  .dependsOn(core, testkit % Test)

lazy val declineJVM = decline.jvm
lazy val declineJS  = decline.js

// Utility command aliases

addCommandAlias("testJVM", "cron4sJVM/test")
addCommandAlias("testJS", "cron4sJS/test")
addCommandAlias("validateJVM",
                ";testJVM;cron4sJVM/mimaReportBinaryIssues;makeMicrosite")
addCommandAlias("validateJS", "testJS")
addCommandAlias("rebuild", ";clean;validateJS;validateJVM")
addCommandAlias("compileAll", ";clean;test:compile")
