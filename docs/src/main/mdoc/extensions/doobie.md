---
layout: docs
title: "Doobie"
section: "extensions"
---

The [`doobie`](https://tpolecat.github.io/doobie/) module adds support for reading and writing `CronExpr` to a database via JDBC. Since JDBC is a JVM-only concept this module can not be used with non-JVM targets. To use it, add the corresponding dependency to your project in SBT:

```scala
libraryDependencies ++= Seq(
  "com.github.alonsodomin.cron4s" %% "cron4s-doobie" % "{{site.cron4sVersion}}",

  "org.tpolecat" %% "doobie-core" % "{{site.doobieVersion}}"
)
```

And now you will need some imports:

```scala mdoc:silent
import cron4s.CronExpr
import cron4s.doobie._
import doobie._
import doobie.implicits._
```

Now define a case class that represents your database data (and which should contain a cron expression):

```scala mdoc
case class MeetingId(value: Long)
case class RecurringMeeting(subject: String, description: String, frequency: CronExpr)
```

With this, you now are capable of writing read and write queries for that given data structure:

```scala mdoc
def loadAllMeetings = {
  sql"select subject, description, frequency from meetings"
    .query[RecurringMeeting]
    .to[List]
}

def updateMeetingFrequency(meetingId: MeetingId, freq: CronExpr) = {
  sql"update meetings set frequency = $freq where meetingId = ${meetingId.value}"
    .update
    .run
}
```

For more information, go to [Doobie's User Guide](https://tpolecat.github.io/doobie/docs/01-Introduction.html).