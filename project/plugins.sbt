resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("org.scala-js"       % "sbt-scalajs"         % "0.6.11")
addSbtPlugin("com.github.gseitz"  % "sbt-release"         % "1.0.3")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"       % "1.3.5")
addSbtPlugin("com.codacy"         % "sbt-codacy-coverage" % "1.3.0")
addSbtPlugin("com.jsuereth"       % "sbt-pgp"             % "1.0.0")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"             % "0.2.11")
addSbtPlugin("org.xerial.sbt"     % "sbt-sonatype"        % "1.1")
addSbtPlugin("com.typesafe.sbt"   % "sbt-site"            % "1.2.0-RC1")