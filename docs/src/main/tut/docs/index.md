---
layout: docs
title: "First Steps"
---

## First Steps

To start using **cron4s** in your project just include the library as part of your dependencies:

```
libraryDependencies += "com.github.alonsodomin.cron4s" %% "cron4s-core" % "x.y.z"
```

Or in ScalaJS:

```
libraryDependencies += "com.github.alonsodomin.cron4s" %%% "cron4s-core" % "x.y.z"
```

### Parsing

**cron4s** uses cron expressions that go from seconds to day of week in the following order:

 * Seconds
 * Minutes
 * Hour Of Day
 * Day Of Month
 * Month
 * Day Of Week

To parse a cron expression into a type that we can work with we will use the `Cron` smart constructor:

```tut
import cron4s._

val parsed = Cron("10-35 2,4,6 * * * *")
```

We will get an `Either[ParseError, CronExpr]`, the right side giving us an error description if
the parsing has failed. Assuming that we have successfully parsed an expression, we can extract it
out of the `Either[..., ...]` with following expression:

```tut
val cron = parsed.right.get
```

**_Note:_** _It is not recommended to use `.right.get` to extract values out of `Either[..., ...]`
types, we are doing it here as a means of simplifying the types just for the sake of the tutorial._ 

### CRON AST

After successfully parsing a CRON expression, the `CronExpr` resulting type represents the previously
parsed expression as an AST, in which we can access all the expression fields individually:

```tut
cron.seconds
cron.minutes
cron.months
```

We can also take the date or time parts only of the expression using either `timePart` or `datePart`:

```tut
val time = cron.timePart
val date = cron.datePart
```

And similarly as with the main expression type, we can get individual fields out of the
date and time sub expressions:
 
```tut
time.seconds
time.minutes

date.daysOfMonth
```

To convert an AST back into the original string expression we simply use the `toString` method:

```tut
cron.toString
```