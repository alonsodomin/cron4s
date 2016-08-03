import sbt._
import Keys._

object Publish {

  lazy val settings = Def.settings(
    publishMavenStyle := true,
    publishArtifact in Test := false,
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