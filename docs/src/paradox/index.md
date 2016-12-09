@@@ index

 * [Usage](usage/index.md)

@@@

# Cron4s

Cron4s is a CRON expression parser entirely written in Scala. Its key features are:

 * Simple AST representation
 * Easy to extend and make it work your preferred date/time library.
 * Can be used in the JVM and JS compiled programs.

## Setup

SBT
:   @@snip [build.sbt](/../buildfile/build.sbt) { #setup_example }

Maven
:   @@snip [pom.xml](/../buildfile/pom.xml) { #setup_example }

Gradle
:   @@snip [build.gradle](/../buildfile/build.gradle) { #setup_example }
