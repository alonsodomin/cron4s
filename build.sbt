import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}
import com.typesafe.tools.mima.core._
import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import scala.xml.transform.{RewriteRule, RuleTransformer}
import microsites._

lazy val consoleImports =
  settingKey[Seq[String]]("Base imports in the console")

// =================================================================================
// Settings
// =================================================================================

inThisBuild(
  Seq(
    name := "cron4s",
    organization := "com.github.alonsodomin.cron4s",
    organizationName := "Antonio Alonso Dominguez",
    description := "CRON expression parser for Scala",
    startYear := Some(2017),
    homepage := Some(url("https://github.com/alonsodomin/cron4s")),
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.txt")),
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
    parallelExecution := false
  )
)

val commonSettings = Def.settings(
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-feature",
    "-unchecked",
    "-deprecation",
    "-explaintypes",
    "-Xlint:-unused,_",
    "-Xfatal-warnings",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials"
  ),
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n == 12 => Seq("-Ypartial-unification")
      case _                       => Nil
    }
  },
  scalacOptions in (Compile, console) := scalacOptions.value.filterNot(
    Set("-Xlint:-unused,_", "-Xfatal-warnings")
  ),
  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
  scalacOptions in Tut := (scalacOptions in (Compile, console)).value,
  apiURL := Some(url("https://alonsodomin.github.io/cron4s/api/")),
  autoAPIMappings := true,
  parallelExecution in Test := false,
  consoleImports := Seq("cron4s._"),
  initialCommands in console := consoleImports.value
    .map(s => s"import $s")
    .mkString("\n")
) ++ CompilerPlugins.All

lazy val commonJvmSettings = Seq(
  fork in Test := true
)

lazy val commonJsSettings = Seq(
  scalaJSStage in Global := FastOptStage,
  // batch mode decreases the amount of memory needed to compile scala.js code
  scalaJSOptimizerOptions := scalaJSOptimizerOptions.value.withBatchMode(isTravisBuild.value),
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
  scalaJSLinkerConfig := scalaJSLinkerConfig.value.withModuleKind(ModuleKind.CommonJSModule),
  jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv()
)

lazy val consoleSettings = Seq(
  consoleImports ++= Seq("java.time._", "cron4s.lib.javatime._")
)

lazy val publishSettings = Seq(
  sonatypeProfileName := "com.github.alonsodomin",
  publishMavenStyle := true,
  publishArtifact in Test := false,
  // don't include scoverage as a dependency in the pom
  // see issue #980
  // this code was copied from https://github.com/mongodb/mongo-spark
  pomPostProcess := { (node: xml.Node) =>
    new RuleTransformer(new RewriteRule {
      override def transform(node: xml.Node): Seq[xml.Node] =
        node match {
          case e: xml.Elem
              if e.label == "dependency" && e.child
                .exists(child => child.label == "groupId" && child.text == "org.scoverage") =>
            Nil
          case _ => Seq(node)
        }
    }).transform(node).head
  }
)

lazy val noPublishSettings = publishSettings ++ Seq(
  skip in publish := true,
  publishArtifact := false,
  mimaFailOnNoPrevious := false
)

lazy val coverageSettings = Seq(
  coverageMinimum := 80,
  coverageFailOnMinimum := true,
  coverageHighlighting := true,
  coverageExcludedPackages := "cron4s\\.bench\\..*"
)

def mimaSettings(module: String): Seq[Setting[_]] =
  Seq(
    mimaPreviousArtifacts := previousStableVersion.value
      .map(organization.value %% s"cron4s-$module" % _)
      .toSet,
    mimaBinaryIssueFilters ++= Seq(
      // Core Exclusions
      ProblemFilters.exclude[DirectMissingMethodProblem]("cron4s.ParseFailed.expected"),
      ProblemFilters.exclude[MissingClassProblem]("cron4s.parser$"),
      ProblemFilters.exclude[MissingClassProblem]("cron4s.parser"),
      ProblemFilters.exclude[MissingClassProblem]("cron4s.CronInterpolator$"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("cron4s.package.Cron4sStringContext"),
      ProblemFilters.exclude[MissingClassProblem]("cron4s.package$Cron4sStringContext"),
      ProblemFilters.exclude[MissingClassProblem]("cron4s.CronInterpolator"),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "cron4s.syntax.AllSyntax.toCronStringInterpolator"
      ),
      ProblemFilters
        .exclude[InheritedNewAbstractMethodProblem]("cron4s.syntax.AllSyntax.embedCronStrings"),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "cron4s.syntax.AllSyntax.cron4s$syntax$CronStringSyntax$_setter_$embedCronStrings_="
      ),
      // Doobie exclusions
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("cron4s.doobie.package.cronExprMeta"),
      // Exclussions due to changes in scalatest and cats-testkit
      ProblemFilters.exclude[Problem]("cron4s.testkit.*")
    )
  )

lazy val docsMappingsAPIDir =
  settingKey[String]("Name of subdirectory in site target directory for api docs")

lazy val docSettings = Seq(
  micrositeName := "Cron4s",
  micrositeDescription := "Scala CRON Parser",
  micrositeHighlightTheme := "atom-one-light",
  micrositeAuthor := "Antonio Alonso Dominguez",
  micrositeGithubOwner := "alonsodomin",
  micrositeGithubRepo := "cron4s",
  micrositeGitterChannel := true,
  micrositeHomepage := "https://alonsodomin.github.io/cron4s",
  micrositeBaseUrl := "cron4s",
  micrositeDocumentationUrl := "/cron4s/api/cron4s/index.html",
  micrositeDocumentationLabelDescription := "API Documentation",
  micrositeExtraMdFiles := Map(
    file("CHANGELOG.md") -> ExtraMdFileConfig(
      "changelog.md",
      "page",
      Map(
        "title"    -> "Change Log",
        "section"  -> "changelog",
        "position" -> "3"
      )
    )
  ),
  micrositePushSiteWith := {
    if (isTravisBuild.value) GitHub4s else GHPagesPlugin
  },
  micrositeGithubToken := sys.env.get("GITHUB_MICROSITES_TOKEN"),
  micrositeConfigYaml := ConfigYml(
    yamlCustomProperties = Map(
      "cron4sVersion"      -> version.value,
      "circeVersion"       -> Dependencies.version.circe,
      "doobieVersion"      -> Dependencies.version.doobie,
      "declineVersion"     -> Dependencies.version.decline,
      "jodaTimeVersion"    -> Dependencies.version.jodaTime,
      "jodaConvertVersion" -> Dependencies.version.jodaConvert,
      "momenttzVersion"    -> Dependencies.version.momenttz,
      "momentjsVersion"    -> Dependencies.version.momentjs
    )
  ),
  micrositeCompilingDocsTool := WithMdoc,
  mdocIn := sourceDirectory.value / "main" / "mdoc",
  fork in Test := true,
  fork in (ScalaUnidoc, unidoc) := true,
  docsMappingsAPIDir := "api",
  addMappingsToSiteDir(
    mappings in (ScalaUnidoc, packageDoc),
    docsMappingsAPIDir
  ),
  ghpagesNoJekyll := false,
  git.remoteRepo := "https://github.com/alonsodomin/cron4s.git",
  unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(
    core.jvm,
    circe.jvm,
    decline.jvm,
    doobie,
    joda,
    momentjs,
    testkit.jvm
  ),
  scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
    "-Xfatal-warnings",
    "-doc-source-url",
    scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
    "-sourcepath",
    baseDirectory.in(LocalRootProject).value.getAbsolutePath,
    "-diagrams"
  )
)

// =================================================================================
// Top level modules
// =================================================================================

lazy val cron4s = (project in file("."))
  .settings(commonSettings)
  .settings(noPublishSettings)
  .aggregate(cron4sJS, cron4sJVM, docs, bench)

lazy val cron4sJS = (project in file(".js"))
  .settings(
    name := "js",
    moduleName := "cron4s-js"
  )
  .settings(commonSettings: _*)
  .settings(commonJsSettings: _*)
  .settings(noPublishSettings)
  .enablePlugins(ScalaJSPlugin)
  .aggregate(core.js, momentjs, circe.js, decline.js, testkit.js, tests.js)
  .dependsOn(core.js, momentjs, circe.js, decline.js, testkit.js, tests.js % Test)

lazy val cron4sJVM = (project in file(".jvm"))
  .settings(
    name := "jvm",
    moduleName := "cron4s-jvm"
  )
  .settings(commonSettings)
  .settings(commonJvmSettings)
  .settings(consoleSettings)
  .settings(noPublishSettings)
  .aggregate(core.jvm, joda, doobie, circe.jvm, decline.jvm, testkit.jvm, tests.jvm)
  .dependsOn(core.jvm, joda, doobie, circe.jvm, decline.jvm, testkit.jvm, tests.jvm % Test)

lazy val docs = project
  .enablePlugins(MicrositesPlugin, ScalaUnidocPlugin, GhpagesPlugin)
  .settings(
    moduleName := "cron4s-docs"
  )
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(docSettings)
  .dependsOn(core.jvm, joda, doobie, circe.jvm, decline.jvm, testkit.jvm)

// =================================================================================
// Main modules
// =================================================================================

lazy val core = (crossProject(JSPlatform, JVMPlatform) in file("modules/core"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin, MimaPlugin)
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
  .jvmSettings(mimaSettings("core"))

lazy val testkit =
  (crossProject(JSPlatform, JVMPlatform) in file("modules/testkit"))
    .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin, MimaPlugin)
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
    .jvmSettings(mimaSettings("testkit"))
    .dependsOn(core)

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

lazy val bench = (project in file("bench"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin)
  .settings(
    name := "bench",
    moduleName := "cron4s-bench"
  )
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(commonJvmSettings)
  .settings(Dependencies.bench)
  .enablePlugins(JmhPlugin)
  .dependsOn(core.jvm)

// =================================================================================
// DateTime library extensions
// =================================================================================

lazy val joda = (project in file("modules/joda"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin, MimaPlugin)
  .settings(
    name := "joda",
    moduleName := "cron4s-joda",
    consoleImports ++= Seq("org.joda.time._", "cron4s.lib.joda._")
  )
  .settings(commonSettings)
  .settings(commonJvmSettings)
  .settings(publishSettings)
  .settings(Dependencies.joda)
  .settings(mimaSettings("joda"))
  .dependsOn(core.jvm, testkit.jvm % Test)

lazy val momentjs = (project in file("modules/momentjs"))
  .enablePlugins(AutomateHeaderPlugin, ScalaJSPlugin, ScalafmtPlugin, ScalaJSBundlerPlugin)
  .settings(commonSettings)
  .settings(commonJsSettings)
  .settings(publishSettings)
  .settings(
    name := "momentjs",
    moduleName := "cron4s-momentjs"
  )
  .settings(Dependencies.momentjs)
  .dependsOn(core.js, testkit.js % Test)

// =================================================================================
// Extension modules
// =================================================================================

lazy val circe =
  (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("modules/circe"))
    .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin, MimaPlugin)
    .settings(
      name := "circe",
      moduleName := "cron4s-circe"
    )
    .settings(commonSettings)
    .settings(publishSettings)
    .settings(Dependencies.circe)
    .settings(mimaSettings("circe"))
    .jvmSettings(commonJvmSettings)
    .jsSettings(commonJsSettings)
    .dependsOn(core, testkit % Test)

lazy val decline =
  (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("modules/decline"))
    .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin, MimaPlugin)
    .settings(
      name := "decline",
      moduleName := "cron4s-decline"
    )
    .settings(commonSettings)
    .settings(publishSettings)
    .settings(Dependencies.decline)
    .settings(mimaSettings("decline"))
    .jvmSettings(commonJvmSettings)
    .jsSettings(commonJsSettings)
    .dependsOn(core, testkit % Test)

lazy val doobie = (project in file("modules/doobie"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin, MimaPlugin)
  .settings(
    name := "doobie",
    moduleName := "cron4s-doobie"
  )
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(commonJvmSettings)
  .settings(mimaSettings("doobie"))
  .settings(Dependencies.doobie)
  .dependsOn(core.jvm, testkit.jvm % Test)

// =================================================================================
// Utility command aliases
// =================================================================================

addCommandAlias("fmt", "scalafmtSbt;scalafmt;test:scalafmt")
addCommandAlias("checkfmt", "scalafmtSbtCheck;scalafmtCheck;test:scalafmtCheck")
addCommandAlias("testJVM", "cron4sJVM/test")
addCommandAlias("testJS", "cron4sJS/test")
addCommandAlias("binCompatCheck", "cron4sJVM/mimaReportBinaryIssues")
addCommandAlias(
  "validateJVM",
  Seq(
    "coverage",
    "testJVM",
    "coverageReport",
    "coverageAggregate"
  ).mkString(";")
)
addCommandAlias("validateJS", "testJS")
addCommandAlias(
  "validate",
  Seq(
    "checkfmt",
    "validateJS",
    "validateJVM",
    "binCompatCheck"
  ).mkString(";")
)
addCommandAlias("rebuild", "clean;validate")
