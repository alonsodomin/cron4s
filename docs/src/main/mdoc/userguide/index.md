---
layout: docs
title: "Getting Started"
section: "userguide"
position: 1
---

To start using **cron4s** in your project just include the library as part of your dependencies:

```scala
libraryDependencies += "com.github.alonsodomin.cron4s" %% "cron4s-core" % "{{site.cron4sVersion}}"
```

Or in ScalaJS:

```scala
libraryDependencies += "com.github.alonsodomin.cron4s" %%% "cron4s-core" % "{{site.cron4sVersion}}"
```

## Parsing

**cron4s** uses cron expressions that go from seconds to day of week in the following order:

 * Seconds
 * Minutes
 * Hour Of Day
 * Day Of Month (1-31)
 * Month (1-12)
 * Day Of Week (0-6, where 0 is Monday)

To parse a cron expression into a type that we can work with we will use the `Cron` smart constructor:

```scala mdoc
import cron4s._

val parsed = Cron("10-35 2,4,6 * ? * *")
```

We will get an `Either[Error, CronExpr]`, the left side giving us an error description if the parsing
has failed. In the above example the expression parsed successfully but if we pass a wrong or invalid expression
we will get the actual reason for the failure in the left side of the `Either`:

```scala mdoc
val invalid = Cron("10-65 * * * * *")
```

```scala mdoc:invisible
assert(invalid.isLeft)
```

If we are not interested in the left side of the result (the error), we can easily convert it into an `Option[CronExpr]`:

```scala mdoc
import cats.syntax.either._

parsed.toOption
invalid.toOption
```

**_Note:_** _In Scala 2.12 you can avoid importing `cats.syntax.either._` to be able to make the conversion._

`Cron(expr)` is just a short-hand for `Cron.parse(expr)`. This object provides also with additional methods for parsing
that return different types. In the first place we have `Cron.tryParse(expr)` which will return a `Try[CronExpr]` instead:

```scala mdoc
val invalidTry = Cron.tryParse("10-65 * * * * *")
```

```scala mdoc:invisible
assert(invalidTry.isFailure)
```

And also `Cron.unsafeParse(expr)`, which will return a _naked_ `CronExpr` or happily blow-up with an exception
interrupting the execution. This is the most Java-friendly version of all of them and you should try to avoid using it
unless you are aware of the consequences (well, it also comes handy during a REPL session or to write this tutorial):

```scala mdoc:crash
Cron.unsafeParse("10-65 * * * * *")
```

### Validation errors

The CRON expression will be validated right after parsing. Any error found during this stage will be returned
as well in the left side of the `Either` returned by the `Cron` constructor. For instance, the following expression
has a sequence that mixes elements that self-imply each other, which is invalid: 

```scala mdoc
val failsValidation = Cron("12,10-20 * * * * ?")
```

```scala mdoc:invisible
assert(failsValidation.isLeft)
```

In this case, the error type in the left side of the `Either` can be narrowed down to `InvalidCron`, which will give
a `NonEmptyList` with all the validation errors that the expression had. To demosrate this, here is an example:

```scala mdoc
failsValidation.swap.foreach { err => err.asInstanceOf[InvalidCron].reason.toList.mkString("\n") }
```
