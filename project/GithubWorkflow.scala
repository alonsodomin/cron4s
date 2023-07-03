import sbtghactions.GenerativePlugin.autoImport._

object GithubWorkflow {
  val DefaultJVM = JavaSpec(JavaSpec.Distribution.Adopt, "8")

  val IsJvm           = "matrix.platform == 'jvm'"
  val IsJs            = "matrix.platform == 'js'"
  val IsTaggedRelease = "startsWith(github.ref, 'refs/tags/v')"

  // SBT Microsites https://47degrees.github.io/sbt-microsites/docs/getting-started/
  val JekyllSetupSteps = Seq(
    WorkflowStep.Use(
      UseRef.Public("actions", "setup-ruby", "v1"),
      params = Map("ruby-version" -> "2.7"),
      cond = Some(IsJvm)
    ),
    WorkflowStep.Run(
      commands = List("gem install jekyll -v 4"),
      name = Some("Configure Jekyll"),
      cond = Some(IsJvm)
    )
  )

  def settings =
    Seq(
      githubWorkflowJavaVersions := Seq(
        DefaultJVM,
        JavaSpec(JavaSpec.Distribution.Adopt, "11"),
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
      githubWorkflowBuildMatrixAdditions += "platform" -> List("jvm", "js"),
      githubWorkflowBuildMatrixExclusions ++=
        githubWorkflowJavaVersions.value.filterNot(Set(DefaultJVM)).flatMap { java =>
          Seq(
            MatrixExclude(Map("platform" -> "js", "java" -> java.render))
          )
        },
      githubWorkflowArtifactUpload := false,
      githubWorkflowBuildPreamble  := JekyllSetupSteps,
      githubWorkflowBuild := Seq(
        WorkflowStep.Sbt(
          List("checkfmt"),
          name = Some("Check source code formatting")
        ),
        WorkflowStep
          .Sbt(List("validateJS"), name = Some("Validate JavaScript"), cond = Some(IsJs)),
        WorkflowStep.Sbt(
          List("validateJVM", "validateBench"),
          name = Some("Validate JVM"),
          cond = Some(IsJvm)
        )
        /*WorkflowStep.Sbt(
          List("clean", "binCompatCheck"),
          name = Some("Binary compatibility ${{ matrix.scala }}"),
          cond = Some(IsJvm)
        )*/
      ),
      githubWorkflowBuildPostamble := Seq(
        WorkflowStep.Sbt(
          List("makeMicrosite"),
          name = Some("Compile documentation"),
          cond = Some(IsJvm)
        )
      ),
      githubWorkflowPublishPreamble := JekyllSetupSteps,
      githubWorkflowPublishPostamble := Seq(
        WorkflowStep.Sbt(
          List("publishMicrosite"),
          name = Some("Publish documentation"),
          cond = Some(IsTaggedRelease)
        )
      )
    )

}
