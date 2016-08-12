import sbt._
import Keys._

import sbtrelease.ReleasePlugin.autoImport._
import ReleaseTransformations._

object Publish {

  lazy val settings = Def.settings(
    publishMavenStyle := true,
    publishArtifact in Test := false,
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
    pomExtra :=
      <url>https://github.com/alonsodomin/cron4s</url>
      <licenses>
        <licence>
          <name>Apache License, Version 2.0</name>
          <url>https://www.apache.org/licenses/LICENSE-2.0</url>
          <distribution>repo</distribution>
        </licence>
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