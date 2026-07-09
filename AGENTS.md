# cron4s – Agent guidance

## Build

- **SBT 1.12.9**, cross-builds to **Scala 2.12.17 / 2.13.18 / 3.3.6** on **JVM, JS, Native**
- `sbt test` runs all platforms; `sbt testJVM` / `testJS` / `testNative` for a single platform
- Cross-build all: `sbt +test`
- **Tests run sequentially** (`parallelExecution := false`)  
- **JVM tests forked** (`Test / fork := true`); `.jvmopts` sets `-Xms1G -Xmx6G`
- Coverage threshold: 90% stmt / 80% branch (fails if below)

## Validation pipeline

```
sbt lint            # scalafmtSbtCheck + scalafmtCheck + Test/scalafmtCheck
sbt validate        # lint -> test all platforms -> bench/compile -> binCompatCheck
sbt rebuild         # clean -> validate
sbt fmt             # scalafmtSbt + scalafmt + Test/scalafmt
```

`sbt validateJVM` also runs coverage (scoverage). `sbt binCompatCheck` runs MiMa.

## CI & publishing

- **CI workflow is auto-generated** – edit `project/GithubWorkflow.scala`, then run `sbt githubWorkflowGenerate` to regenerate `.github/workflows/ci.yml`. Never edit `ci.yml` by hand.
- Publishing via `sbt-ci-release`: tags push to Maven Central, master commits → snapshots.

## Key module layout

| dir | module | platforms |
|---|---|---|
| `modules/core` | cron4s-core | JVM+JS+Native |
| `modules/parser` | cron4s-parser | JVM+JS+Native |
| `modules/parserc` | cron4s-parserc (parser combinators) | JVM+JS+Native |
| `modules/atto` | cron4s-atto (atto parsers) | JVM+JS |
| `modules/testkit` | cron4s-testkit | JVM+JS+Native |
| `modules/circe` | cron4s-circe | JVM+JS+Native |
| `modules/decline` | cron4s-decline | JVM+JS+Native |
| `modules/doobie` | cron4s-doobie | JVM only |
| `modules/joda` | cron4s-joda | JVM only |
| `modules/momentjs` | cron4s-momentjs | JS only |
| `bench/` | JMH benchmarks (via sbt-jmh) | JVM only |
| `tests/` | cross-tests | JVM+JS+Native |

## Style

- scalafmt 3.10.7, dialect Scala213 by default (Scala3 for `scala-3/` dirs), `maxColumn = 100`, `style = defaultWithAlign`
- Compiler plugins per Scala version (see `project/CompilerPlugins.scala`): kind-projector + better-monadic-for on 2.x; macro-paradise on 2.12 only; none on 3.x
- `-Xfatal-warnings` on by default (relaxed in console)

## Benchmarks

```sh
sbt "bench/jmh:run -r 2 -i 10 -w 2 -wi 10 -f 1 -t 1 cron4s.bench.*Benchmark"
```

Also via `./runBench.sh [name]`.

## Docs site

```sh
sbt makeMicrosite
cd docs/target/site && jekyll serve
```

Requires Jekyll. Uses sbt-microsites.

## MiMA

Binary compatibility checked for published modules. Exclusions in `build.sbt:mimaSettings`. Run `sbt binCompatCheck` (aliased to `cron4sJVM/mimaReportBinaryIssues`).
