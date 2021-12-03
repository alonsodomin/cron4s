resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.7.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.1.0")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"            % "1.8.2")
addSbtPlugin("com.codacy"         % "sbt-codacy-coverage"      % "3.0.3")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"                  % "0.4.3")
addSbtPlugin("com.47deg"          % "sbt-microsites"           % "1.3.4")
addSbtPlugin("com.eed3si9n"       % "sbt-unidoc"               % "0.4.3")
addSbtPlugin("com.typesafe"       % "sbt-mima-plugin"          % "0.7.0")
addSbtPlugin("de.heikoseeberger"  % "sbt-header"               % "5.6.0")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"             % "2.4.5")
addSbtPlugin("com.geirsson"       % "sbt-ci-release"           % "1.5.7")
addSbtPlugin("ch.epfl.scala"      % "sbt-scalajs-bundler"      % "0.20.0")
addSbtPlugin("com.codecommit"     % "sbt-github-actions"       % "0.12.0")
