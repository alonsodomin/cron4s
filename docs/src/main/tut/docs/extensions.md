---
layout: docs
title: "Extensions"
---

## Extensions

`cron4s` provides a series of extensions that add interoperability with other commonly used libraries. These
extensions are trivial enough to not be a big deal to be added to the host application, but by using them you
will get some properly tested code and prevent code duplication across projects.

### Circe

The [`circe`](http://circe.io) extension adds the possibility of using `CronExpr` in JSON messages. To use it
just add the following to your dependencies:

```
libraryDependencies += "com.github.alonsodomin.cron4s" %% "cron4s-circe" % "x.y.z"
```

And add the correct import:

```tut:silent
import cron4s.circe._
```

### Decline

The [`decline`](http://ben.kirw.in/decline/) extension adds the ability of using `CronExpr` as
an option in the command line. To be able to use it add the following dependency to your classpath:

```
libraryDependencies += "com.github.alonsodomin.cron4s" %% "cron4s-decline" % "x.y.z"
```

And now you will need some imports:

```tut:silent
import cron4s.expr.CronExpr
import cron4s.decline._
import com.monovore.decline._
```

Now you can define options based on cron expressions:

```tut:book
val cronOpt = Opts.option[CronExpr]("frequency", "A cron expression")
```