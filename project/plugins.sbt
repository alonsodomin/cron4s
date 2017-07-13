resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("org.scala-js"       % "sbt-scalajs"         % "0.6.17")
addSbtPlugin("com.github.gseitz"  % "sbt-release"         % "1.0.4")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"       % "1.5.0")
addSbtPlugin("com.codacy"         % "sbt-codacy-coverage" % "1.3.0")
addSbtPlugin("com.jsuereth"       % "sbt-pgp"             % "1.0.0")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"             % "0.2.25")
addSbtPlugin("org.xerial.sbt"     % "sbt-sonatype"        % "1.1")
addSbtPlugin("com.47deg"          % "sbt-microsites"      % "0.6.0")
addSbtPlugin("com.eed3si9n"       % "sbt-unidoc"          % "0.4.0")
addSbtPlugin("com.typesafe"       % "sbt-mima-plugin"     % "0.1.11")
addSbtPlugin("de.heikoseeberger"  % "sbt-header"          % "1.6.0")
addSbtPlugin("com.lucidchart"     % "sbt-scalafmt"        % "1.8")