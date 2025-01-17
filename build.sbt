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

Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  Seq(
    organization       := "com.github.alonsodomin.cron4s",
    organizationName   := "Antonio Alonso Dominguez",
    description        := "CRON expression parser for Scala",
    startYear          := Some(2017),
    crossScalaVersions := Seq("2.13.15", "2.12.17", "3.3.4"),
    homepage           := Some(url("https://github.com/alonsodomin/cron4s")),
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.txt")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/alonsodomin/cron4s"),
        "scm:git:git@github.com:alonsodomin/cron4s.git"
      )
    ),
    developers ++= List(
      Developer(
        "alonsodomin",
        "A. Alonso Dominguez",
        "work@alonsodomin.me",
        url("https://github.com/alonsodomin")
      ),
      Developer(
        "i110416",
        "110416",
        "contact.110416@gmail.com",
        url("https://github.com/i10416")
      )
    ),
    libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always,
    versionScheme                                        := Some("semver-spec")
  ) ++ GithubWorkflow.settings
)

val commonSettings = Def.settings(
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-feature",
    "-unchecked",
    "-deprecation",
    "-explaintypes",
    "-Xfatal-warnings",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials"
  ),
  scalacOptions ++= (if (scalaVersion.value.startsWith("2.")) {
                       Seq(
                         "-Xlint:-unused,_"
                       )
                     } else
                       Seq(
                         "-Wunused:imports",
                         "-Wunused:locals",
                         "-Wunused:implicits",
                         "-Wunused:privates"
                       )),
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n == 12 => Seq("-Ypartial-unification")
      case Some((2, n)) if n > 12 =>
        Seq("-Xlint:-byname-implicit", "-Ymacro-annotations")
      case _ => Nil
    }
  },
  Compile / console / scalacOptions := scalacOptions.value.filterNot(
    Set("-Xlint:-unused,_", "-Xfatal-warnings")
  ),
  Test / console / scalacOptions := (Compile / console / scalacOptions).value,
  apiURL                         := Some(url("https://alonsodomin.github.io/cron4s/api/")),
  autoAPIMappings                := true,
  Test / parallelExecution       := false,
  consoleImports                 := Seq("cron4s._"),
  console / initialCommands := consoleImports.value
    .map(s => s"import $s")
    .mkString("\n") + "\n"
) ++ CompilerPlugins.All

lazy val commonJvmSettings = Seq(
  Test / fork := true
)

lazy val commonJsSettings = Seq(
  Global / scalaJSStage := FastOptStage,
  scalacOptions ++= (if (scalaVersion.value.startsWith("2.")) Seq {
                       val tagOrHash = {
                         if (isSnapshot.value)
                           sys.process.Process("git rev-parse HEAD").lineStream_!.head
                         else version.value
                       }
                       val a = (LocalRootProject / baseDirectory).value.toURI.toString
                       val g = "https://raw.githubusercontent.com/alonsodomin/cron4s/" + tagOrHash
                       s"-P:scalajs:mapSourceURI:$a->$g/"
                     }
                     else Seq.empty),
  scalaJSLinkerConfig := scalaJSLinkerConfig.value.withModuleKind(ModuleKind.CommonJSModule),
  jsEnv               := new org.scalajs.jsenv.nodejs.NodeJSEnv()
)

lazy val consoleSettings = Seq(
  consoleImports ++= Seq("java.time._", "cron4s.lib.javatime._")
)

lazy val publishSettings = Seq(
  sonatypeProfileName    := "com.github.alonsodomin",
  publishMavenStyle      := true,
  Test / publishArtifact := false,
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
  publish / skip       := true,
  publishArtifact      := false,
  mimaFailOnNoPrevious := false
)

lazy val coverageSettings = Seq(
  coverageMinimumStmtTotal   := 90,
  coverageMinimumBranchTotal := 80,
  coverageFailOnMinimum      := true,
  coverageHighlighting       := true,
  coverageExcludedPackages   := "cron4s\\.bench\\..*"
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
      ProblemFilters.exclude[MissingTypesProblem]("cron4s.ParseFailed$"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("cron4s.ParseFailed.tupled"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("cron4s.ParseFailed.curried"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("cron4s.ParseFailed.msg"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("cron4s.ParseFailed.*"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("cron4s.ParseFailed.*"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("cron4s.parsing.*.handleError"),
      // Doobie exclusions
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("cron4s.doobie.package.cronExprMeta"),
      // Exclussions due to changes in scalatest and cats-testkit
      ProblemFilters.exclude[Problem]("cron4s.testkit.*")
    )
  )

lazy val docsMappingsAPIDir =
  settingKey[String]("Name of subdirectory in site target directory for api docs")

lazy val docSettings = Seq(
  micrositeName                          := "Cron4s",
  micrositeDescription                   := "Scala CRON Parser",
  micrositeHighlightTheme                := "atom-one-light",
  micrositeAuthor                        := "Antonio Alonso Dominguez",
  micrositeGithubOwner                   := "alonsodomin",
  micrositeGithubRepo                    := "cron4s",
  micrositeGitterChannel                 := true,
  micrositeUrl                           := "https://www.alonsodomin.me",
  micrositeBaseUrl                       := "/cron4s",
  micrositeHomepage                      := "https://www.alonsodomin.me/cron4s/",
  micrositeDocumentationUrl              := "/cron4s/api/cron4s/index.html",
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
    if (githubIsWorkflowBuild.value) GitHub4s else GHPagesPlugin
  },
  micrositeGithubToken := sys.env.get("GITHUB_TOKEN"),
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
  mdocIn             := sourceDirectory.value / "main" / "mdoc",
  Test / fork        := true,
  docsMappingsAPIDir := "api",
  addMappingsToSiteDir(
    ScalaUnidoc / packageDoc / mappings,
    docsMappingsAPIDir
  ),
  ghpagesNoJekyll := false,
  git.remoteRepo  := "https://github.com/alonsodomin/cron4s.git",
  ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(
    core.jvm,
    circe.jvm,
    decline.jvm,
    doobie,
    joda,
    momentjs,
    testkit.jvm
  ),
  ScalaUnidoc / unidoc / scalacOptions ++= Seq(
    "-Xfatal-warnings",
    "-doc-source-url",
    scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
    "-sourcepath",
    (LocalRootProject / baseDirectory).value.getAbsolutePath,
    "-groups",
    "-diagrams"
  )
)

// =================================================================================
// Top level modules
// =================================================================================

lazy val cron4s = (project in file("."))
  .settings(commonSettings)
  .settings(noPublishSettings)
  .aggregate(cron4sJS, cron4sJVM, cron4sNative, docs, bench)

lazy val cron4sJS = (project in file(".js"))
  .settings(
    name       := "js",
    moduleName := "cron4s-js"
  )
  .settings(commonSettings: _*)
  .settings(commonJsSettings: _*)
  .settings(noPublishSettings)
  .enablePlugins(ScalaJSPlugin)
  .aggregate(core.js, parser.js, atto.js, momentjs, circe.js, decline.js, testkit.js, tests.js)
  .dependsOn(
    core.js,
    parser.js,
    atto.js,
    momentjs,
    circe.js,
    decline.js,
    testkit.js,
    tests.js % Test
  )

lazy val cron4sJVM = (project in file(".jvm"))
  .settings(
    name       := "jvm",
    moduleName := "cron4s-jvm"
  )
  .settings(commonSettings)
  .settings(commonJvmSettings)
  .settings(consoleSettings)
  .settings(noPublishSettings)
  .aggregate(
    parser.jvm,
    core.jvm,
    atto.jvm,
    joda,
    doobie,
    circe.jvm,
    decline.jvm,
    testkit.jvm,
    tests.jvm
  )
  .dependsOn(
    core.jvm,
    joda,
    doobie,
    circe.jvm,
    decline.jvm,
    testkit.jvm,
    tests.jvm % Test
  )

lazy val cron4sNative = (project in file(".native"))
  .settings(
    name       := "native",
    moduleName := "cron4s-native"
  )
  .settings(commonSettings)
  .settings(noPublishSettings)
  .aggregate(
    core.native,
    circe.native,
    decline.native,
    testkit.native,
    tests.native
  )
  .dependsOn(
    core.native,
    circe.native,
    decline.native,
    testkit.native,
    tests.native % Test
  )

lazy val docs = project
  .enablePlugins(MicrositesPlugin, ScalaUnidocPlugin, GhpagesPlugin)
  .settings(
    moduleName := "cron4s-docs"
  )
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(docSettings)
  .dependsOn(core.jvm, parserc.jvm, joda, doobie, circe.jvm, decline.jvm, testkit.jvm)

// =================================================================================
// Main modules
// =================================================================================

lazy val parser = (crossProject(JSPlatform, JVMPlatform, NativePlatform) in file("modules/parser"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin, MimaPlugin)
  .settings(
    name       := "parser",
    moduleName := "cron4s-parser"
  )
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(Dependencies.core)
  .jsSettings(commonJsSettings)
  .jsSettings(Dependencies.coreJS)
  .jvmSettings(commonJvmSettings)
  .jvmSettings(consoleSettings)
  .jvmSettings(Dependencies.coreJVM)
  .jvmSettings(mimaSettings("parser"))
  .nativeSettings(Dependencies.coreNative)

lazy val core = (crossProject(JSPlatform, JVMPlatform, NativePlatform) in file("modules/core"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin, MimaPlugin)
  .settings(
    name       := "core",
    moduleName := "cron4s-core"
  )
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(Dependencies.core)
  .jsSettings(commonJsSettings)
  .jsSettings(Dependencies.coreJS)
  .jsConfigure(_.dependsOn(atto.js))
  .jvmSettings(commonJvmSettings)
  .jvmSettings(consoleSettings)
  .jvmSettings(Dependencies.coreJVM)
  .jvmSettings(mimaSettings("core"))
  .jvmConfigure(_.dependsOn(atto.jvm))
  .nativeSettings(Dependencies.coreNative)
  .nativeConfigure(_.dependsOn(parserc.native))
  .dependsOn(parser)

lazy val parserc =
  (crossProject(JSPlatform, JVMPlatform, NativePlatform) in file("modules/parserc"))
    .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin, MimaPlugin)
    .settings(
      name       := "parserc",
      moduleName := "cron4s-parserc"
    )
    .settings(commonSettings)
    .settings(publishSettings)
    .settings(Dependencies.parserc)
    .jsSettings(commonJsSettings)
    .jsSettings(Dependencies.coreJS)
    .jvmSettings(commonJvmSettings)
    .jvmSettings(Dependencies.coreJVM)
    .nativeSettings(Dependencies.coreNative)
    .dependsOn(parser)

lazy val atto = (crossProject(JSPlatform, JVMPlatform) in file("modules/atto"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin, MimaPlugin)
  .settings(
    name       := "atto",
    moduleName := "cron4s-atto"
  )
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(Dependencies.atto)
  .jsSettings(commonJsSettings)
  .jsSettings(Dependencies.coreJS)
  .jvmSettings(commonJvmSettings)
  .jvmSettings(Dependencies.coreJVM)
  .dependsOn(parser)

lazy val testkit =
  (crossProject(JSPlatform, JVMPlatform, NativePlatform) in file("modules/testkit"))
    .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin, MimaPlugin)
    .settings(
      name       := "testkit",
      moduleName := "cron4s-testkit"
    )
    .settings(commonSettings)
    .settings(publishSettings)
    .settings(Dependencies.testkit)
    .jsSettings(commonJsSettings)
    .jvmSettings(commonJvmSettings)
    .jvmSettings(consoleSettings)
    .jvmSettings(Dependencies.coreJVM)
    .jvmSettings(mimaSettings("testkit"))
    .nativeSettings(Dependencies.coreNative)
    .dependsOn(core, parserc)

lazy val tests = (crossProject(JSPlatform, JVMPlatform, NativePlatform) in file("tests"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin)
  .settings(
    name       := "tests",
    moduleName := "cron4s-tests"
  )
  .settings(commonSettings)
  .settings(noPublishSettings)
  .jsSettings(commonJsSettings)
  .jsSettings(Dependencies.testsJS)
  .jvmSettings(commonJvmSettings)
  .jvmSettings(Dependencies.testsJVM)
  .dependsOn(testkit % Test)

lazy val bench = (project in file("bench"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin)
  .settings(
    name       := "bench",
    moduleName := "cron4s-bench"
  )
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(commonJvmSettings)
  .enablePlugins(JmhPlugin)
  .dependsOn(core.jvm, parserc.jvm)

// =================================================================================
// DateTime library extensions
// =================================================================================

lazy val joda = (project in file("modules/joda"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin, MimaPlugin)
  .settings(
    name       := "joda",
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
    name       := "momentjs",
    moduleName := "cron4s-momentjs"
  )
  .settings(Dependencies.momentjs)
  .dependsOn(core.js, testkit.js % Test)

// =================================================================================
// Extension modules
// =================================================================================

lazy val circe =
  (crossProject(JSPlatform, JVMPlatform, NativePlatform).crossType(CrossType.Pure) in file(
    "modules/circe"
  ))
    .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin, MimaPlugin)
    .settings(
      name       := "circe",
      moduleName := "cron4s-circe",
      consoleImports ++= Seq("cron4s.circe._", "io.circe._", "io.circe.syntax._")
    )
    .settings(commonSettings)
    .settings(publishSettings)
    .settings(Dependencies.circe)
    .settings(mimaSettings("circe"))
    .jvmSettings(commonJvmSettings)
    .jvmSettings(Dependencies.coreJVM)
    .jsSettings(commonJsSettings)
    .nativeSettings(Dependencies.coreNative)
    .dependsOn(core, parserc, testkit % Test)

lazy val decline =
  (crossProject(JSPlatform, JVMPlatform, NativePlatform).crossType(CrossType.Pure) in file(
    "modules/decline"
  ))
    .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin, MimaPlugin)
    .settings(
      name       := "decline",
      moduleName := "cron4s-decline",
      consoleImports ++= Seq("cron4s.decline._", "com.monovore.decline._")
    )
    .settings(commonSettings)
    .settings(publishSettings)
    .settings(Dependencies.decline)
    .settings(mimaSettings("decline"))
    .jvmSettings(commonJvmSettings)
    .jvmSettings(Dependencies.coreJVM)
    .jsSettings(commonJsSettings)
    .nativeSettings(Dependencies.coreNative)
    .dependsOn(core, parserc, testkit % Test)

lazy val doobie = (project in file("modules/doobie"))
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin, MimaPlugin)
  .settings(
    name       := "doobie",
    moduleName := "cron4s-doobie",
    consoleImports ++= Seq("cron4s.doobie._", "doobie._", "doobie.implicits._")
  )
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(commonJvmSettings)
  .settings(mimaSettings("doobie"))
  .settings(Dependencies.doobie)
  .settings(Dependencies.coreJVM)
  .dependsOn(core.jvm, parserc.jvm, testkit.jvm % Test)

// =================================================================================
// Utility command aliases
// =================================================================================

def addCompoundCommandAlias(alias: String)(subcommands: String*) =
  addCommandAlias(alias, subcommands.mkString(";"))

addCompoundCommandAlias("fmt")("scalafmtSbt", "scalafmt", "Test/scalafmt")
addCompoundCommandAlias("lint")("scalafmtSbtCheck", "scalafmtCheck", "Test/scalafmtCheck")
addCommandAlias("testJVM", "cron4sJVM/test")
addCommandAlias("testJS", "cron4sJS/test")
addCommandAlias("testNative", "cron4sNative/test")
addCommandAlias("binCompatCheck", "cron4sJVM/mimaReportBinaryIssues")
addCompoundCommandAlias("validateJVM")(
  "coverage",
  "testJVM",
  "coverageReport",
  "coverageAggregate"
)
addCommandAlias("validateJS", "testJS")
addCommandAlias("validateNative", "testNative")
addCommandAlias("validateBench", "bench/compile")
addCompoundCommandAlias("validate")(
  "lint",
  "validateJS",
  "validateJVM",
  "validateNative",
  "validateBench",
  "binCompatCheck"
)
addCompoundCommandAlias("rebuild")("clean", "validate")
