
import scala.xml.transform.{RewriteRule, RuleTransformer}

scalaVersion in ThisBuild := "2.11.8"

// TODO The parser combinators lib needs to support multiple Scala/ScalaJS versions to enable this
//crossScalaVersions in ThisBuild := Seq("2.10.4", "2.11.8", "2.12.0-M5")

val globalSettings = Def.settings(
  name := "cron4s",
  organization := "com.github.alonsodomin",
  scalacOptions ++= Seq(
    "-language:postfixOps",
    "-feature",
    "-unchecked",
    "-deprecation",
    "-Xfuture",
    "-Xlint",
    "-Xfatal-warnings",
    "-Ywarn-dead-code",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials"
  ),
  releasePublishArtifactsAction := PgpKeys.publishSigned.value
) ++ Publish.settings

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

lazy val coverageSettings = Seq(
  coverageMinimum := 60,
  coverageFailOnMinimum := false,
  coverageHighlighting := true,
  coverageExcludedPackages := "cron4s\\.bench\\..*",
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
  }
)

lazy val cron4s = (project in file(".")).
  settings(globalSettings).
  settings(noPublishSettings).
  aggregate(cron4sJS, cron4sJVM)

lazy val cron4sJS = (project in file(".js")).
  settings(
    name := "cron4s",
    moduleName := "cron4s"
  ).
  settings(globalSettings: _*).
  settings(commonJsSettings: _*).
  enablePlugins(ScalaJSPlugin).
  aggregate(typesJS, coreJS).
  dependsOn(typesJS, coreJS)

lazy val cron4sJVM = (project in file(".jvm")).
  settings(
    name := "cron4s",
    moduleName := "cron4s"
  ).
  settings(globalSettings: _*).
  aggregate(typesJVM, coreJVM).
  dependsOn(typesJVM, coreJVM)

lazy val types = (crossProject.crossType(CrossType.Pure) in file("types")).
  settings(
    name := "types",
    moduleName := "cron4s-types"
  ).
  settings(globalSettings: _*).
  settings(commonJsSettings: _*).
  settings(Dependencies.types: _*)

lazy val typesJS = types.js
lazy val typesJVM = types.jvm

lazy val core = (crossProject in file("core")).
  settings(
    name := "core",
    moduleName := "cron4s-core"
  ).
  settings(globalSettings: _*).
  settings(commonJsSettings: _*).
  settings(Dependencies.core: _*).
  jvmSettings(Dependencies.coreJVM: _*).
  jsSettings(Dependencies.coreJS: _*).
  dependsOn(types)

lazy val coreJS = core.js
lazy val coreJVM = core.jvm

lazy val bench = (project in file("bench")).
  settings(name := "bench").
  settings(globalSettings).
  settings(noPublishSettings).
  enablePlugins(JmhPlugin).
  dependsOn(coreJVM)
