---
layout: docs
title: "Circe"
section: "extensions"
---

The [`circe`](http://circe.io) extension adds the possibility of using `CronExpr` in JSON messages. To use it
just add the following to your dependencies:

```scala
libraryDependencies += "com.github.alonsodomin.cron4s" %% "cron4s-circe" % "x.y.z"
```

And add the correct import:

```tut:silent
import cron4s._
import cron4s.circe._
import io.circe._
import io.circe.syntax._
```

This should be enough to be able to encode and decode cron expressions as JSON with Circe:

```tut:book
val cron = Cron.unsafeParse("10-35 2,4,6 * ? * *")

val jsonCron = cron.asJson
jsonCron.as[CronExpr]
```