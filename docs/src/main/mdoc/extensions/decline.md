---
layout: docs
title: "Decline"
section: "extensions"
---

The [`decline`](http://ben.kirw.in/decline/) extension adds the ability of using `CronExpr` as
an option in the command line. To be able to use it add the following dependency to your classpath:

```scala
libraryDependencies ++= Seq(
  "com.github.alonsodomin.cron4s" %% "cron4s-decline" % "{{site.cron4sVersion}}",

  "com.monovore" %% "decline" % "{{site.declineVersion}}"
)
```

And now you will need some imports:

```scala mdoc:silent
import cron4s.CronExpr
import cron4s.decline._
import com.monovore.decline._
```

Now you can define options based on cron expressions:

```scala mdoc
val cronOpt = Opts.option[CronExpr]("frequency", "A cron expression")
```