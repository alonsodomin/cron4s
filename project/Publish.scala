import sbt._
import Keys._

import xerial.sbt.Sonatype.autoImport._

import sbtrelease.ReleasePlugin.autoImport._
import ReleaseTransformations._

object Publish {

  lazy val settings = Def.settings(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    sonatypeProfileName := "com.github.alonsodomin",
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      ReleaseStep(action = Command.process("publishSigned", _)),
      setNextVersion,
      commitNextVersion,
      ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
      pushChanges
    ),
    publishTo <<= version { v: String =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
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

}