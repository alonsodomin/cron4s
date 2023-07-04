import sbtghactions.GenerativePlugin.autoImport._

object GithubWorkflow {
  val DefaultJVM = JavaSpec(JavaSpec.Distribution.Adopt, "8")

  val JvmCond = s"matrix.platform == 'jvm'"
  val JsCond  = s"matrix.platform == 'js'"

  def settings =
    Seq(
      githubWorkflowJavaVersions := Seq(
        DefaultJVM,
        JavaSpec(JavaSpec.Distribution.Adopt, "11"),
        JavaSpec(JavaSpec.Distribution.Temurin, "17")
      ),
      githubWorkflowTargetBranches := Seq("master"),
      githubWorkflowTargetTags ++= Seq("v*"),
      githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Tag("v"))),
      githubWorkflowPublish := Seq(
        WorkflowStep.Sbt(
          List("ci-release"),
          env = Map(
            "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE }}",
            "PGP_SECRET"        -> "${{ secrets.PGP_SECRET }}",
            "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
            "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
          )
        )
      ),
      githubWorkflowBuildMatrixAdditions +=
        "platform" -> List("jvm", "js"),
      githubWorkflowBuildMatrixExclusions ++=
        githubWorkflowJavaVersions.value.filterNot(Set(DefaultJVM)).flatMap { java =>
          Seq(
            MatrixExclude(Map("platform" -> "js", "java" -> java.render))
          )
        },
      githubWorkflowArtifactUpload := false,
      githubWorkflowBuild := Seq(
        WorkflowStep
          .Sbt(List("validateJS"), name = Some("Validate JavaScript"), cond = Some(JsCond)),
        WorkflowStep.Sbt(
          List("validateJVM", "validateBench"),
          name = Some("Validate JVM"),
          cond = Some(JvmCond)
        )
        /*WorkflowStep.Sbt(
          List("clean", "binCompatCheck"),
          name = Some("Binary compatibility ${{ matrix.scala }}"),
          cond = Some(JvmCond)
        )*/
      )
    )

}
