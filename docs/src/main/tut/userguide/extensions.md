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
import cron4s.CronExpr
import cron4s.decline._
import com.monovore.decline._
```

Now you can define options based on cron expressions:

```tut:book
val cronOpt = Opts.option[CronExpr]("frequency", "A cron expression")
```

### Doobie

The [`doobie`](https://tpolecat.github.io/doobie/) module adds support for reading and writing `CronExpr` to a database via JDBC. Since JDBC is a JVM-only concept this module can not be used with non-JVM targets. To use it, add the corresponding dependency to your project in SBT:

```
libraryDependencies += "com.github.alonsodomin.cron4s" %% "cron4s-doobie" % "x.y.z"
```

And now you will need some imports:

```tut:silent
import cron4s.CronExpr
import cron4s.doobie._
import doobie._
import doobie.implicits._
```

Now define a case class that represents your database data (and which should contain a cron expression):

```tut:book
case class MeetingId(value: Long) extends AnyVal
case class RecurringMeeting(subject: String, description: String, frequency: CronExpr)
```

With this, you now are capable of writing read and write queries for that given data structure:

```tut:book
def loadAllMeetings =
  sql"select subject, description, frequency from meetings"
    .query[RecurringMeeting]
    .to[List]

def updateMeetingFrequency(meetingId: MeetingId, freq: CronExpr) =
  sql"update meetings set frequency = $freq where meetingId = ${meetingId.value}"
    .update
    .quick
```

For more information, go to [Doobie's User Guide](https://tpolecat.github.io/doobie/docs/01-Introduction.html).