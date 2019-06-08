resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.0.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"            % "1.5.1")
addSbtPlugin("com.codacy"         % "sbt-codacy-coverage"      % "2.112")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"                  % "0.3.6")
addSbtPlugin("com.47deg"          % "sbt-microsites"           % "0.9.0")
addSbtPlugin("com.eed3si9n"       % "sbt-unidoc"               % "0.4.2")
addSbtPlugin("com.typesafe"       % "sbt-mima-plugin"          % "0.3.0")
addSbtPlugin("de.heikoseeberger"  % "sbt-header"               % "5.2.0")
addSbtPlugin("com.geirsson"       % "sbt-scalafmt"             % "1.5.1")
addSbtPlugin("com.dwijnand"       % "sbt-travisci"             % "1.2.0")
addSbtPlugin("com.geirsson"       % "sbt-ci-release"           % "1.2.2")
