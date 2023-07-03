import sbtghactions.GenerativePlugin.autoImport._

object GithubWorkflow {
  val DefaultJVM = JavaSpec(JavaSpec.Distribution.Adopt, "8")

  val JvmCond = s"matrix.platform == 'jvm'"
  val JsCond  = s"matrix.platform == 'js'"

  // SBT Microsites https://47degrees.github.io/sbt-microsites/docs/getting-started/
  val JekyllSetupSteps = Seq(
    WorkflowStep.Use(
      UseRef.Public("actions", "setup-ruby", "v1"),
      params = Map("ruby-version" -> "2.6"),
      cond = Some(JvmCond)
    ),
    WorkflowStep.Run(
      commands = List("gem install jekyll -v 4"),
      name = Some("Configure Jekyll"),
      cond = Some(JvmCond)
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
      ),
      githubWorkflowBuildPostamble := Seq(
        WorkflowStep.Sbt(
          List("makeMicrosite"),
          name = Some("Compile documentation"),
          cond = Some(JvmCond)
        )
      ),
      githubWorkflowPublishPreamble := JekyllSetupSteps,
      githubWorkflowPublishPostamble := Seq(
        WorkflowStep.Sbt(
          List("publishMicrosite"),
          name = Some("Publish documentation"),
          cond = Some("startsWith(github.ref, 'refs/tags/v')")
        )
      )
    )

}
