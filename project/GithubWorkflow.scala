import sbtghactions.GenerativePlugin.autoImport._

object GithubWorkflow {
  val DefaultJVM = JavaSpec(JavaSpec.Distribution.Temurin, "17")

  val IsJvm    = "matrix.platform == 'jvm'"
  val IsJs     = "matrix.platform == 'js'"
  val IsNative = "matrix.platform == 'native'"

  def settings =
    Seq(
      githubWorkflowJavaVersions := Seq(
        JavaSpec(JavaSpec.Distribution.Adopt, "8"),
        DefaultJVM,
        JavaSpec(JavaSpec.Distribution.Temurin, "17")
      ),
      githubWorkflowTargetBranches := Seq("master"),
      githubWorkflowTargetTags ++= Seq("v*"),
      githubWorkflowPublishTargetBranches := Seq(
        RefPredicate.StartsWith(Ref.Tag("v")),
        RefPredicate.Equals(Ref.Branch("main"))
      ),
      githubWorkflowPublish := Seq(
        WorkflowStep.Sbt(
          List("ci-release"),
          name = Some("Publish library"),
          env = Map(
            "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE }}",
            "PGP_SECRET"        -> "${{ secrets.PGP_SECRET }}",
            "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
            "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
          )
        )
      ),
      githubWorkflowBuildMatrixAdditions += "platform" -> List("jvm", "js", "native"),
      githubWorkflowBuildMatrixExclusions ++=
        githubWorkflowJavaVersions.value.filterNot(Set(DefaultJVM)).flatMap { java =>
          Seq(
            MatrixExclude(Map("platform" -> "js", "java" -> java.render)),
            MatrixExclude(Map("platform" -> "native", "java" -> java.render))
          )
        },
      githubWorkflowArtifactUpload := false,
      githubWorkflowBuild := Seq(
        WorkflowStep.Sbt(
          List("lint"),
          name = Some("Lint source code")
        ),
        WorkflowStep
          .Sbt(List("validateJS"), name = Some("Validate JavaScript"), cond = Some(IsJs)),
        WorkflowStep.Sbt(
          List("validateJVM", "validateBench"),
          name = Some("Validate JVM"),
          cond = Some(IsJvm)
        ),
        WorkflowStep.Sbt(
          List("validateNative"),
          name = Some("Validate Native"),
          cond = Some(IsNative)
        )
        /*WorkflowStep.Sbt(
          List("clean", "binCompatCheck"),
          name = Some("Binary compatibility ${{ matrix.scala }}"),
          cond = Some(IsJvm)
        )*/
      )
    )

}
