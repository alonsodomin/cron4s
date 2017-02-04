---
layout: docs
title: "Matching Date & Time"
---

# Matching Date & Time

To be able to show examples of how to use **cron4s**, we are going to base them in the brand
new Java Time API added to Java 8 as is one of the bests (if not the best) date & time
libraries available. To use the built-in support for it we will need the following imports:

```tut:silent
import java.time._
import cron4s._
import cron4s.lib.javatime._
```

Now let's parse a new expression:

```tut
val Right(cron) = Cron("10-35 2,4,6 * * * *")
```

Now we can perform 2 types of matches against any object that extends `java.time.Temporal`:

```tut
val now = LocalDateTime.of(2016, 12, 1, 0, 4, 9)

cron.allOf(now)
cron.anyOf(now)
```

As said before, these two operations are also available at the sub-expression level:

```tut
cron.datePart.allOf(now)
cron.datePart.anyOf(now)
cron.timePart.allOf(now)
cron.timePart.anyOf(now)
```

`allOf` will evaluate all the fields of the Cron expression against all the fields
of the _DateTime_ object and it will return `false` if there is any one that does
not match the sub-expression corresponding to that field. In the other hand, `anyOf`
will return `true` if there is at least one field in the Cron expression that matches
it corresponding field.

### Individual Fields

We can also test if the fields that compound the expression match separately instead
of using the whole expression:

```tut
cron.seconds.matchesIn(now)
cron.minutes.matchesIn(now)
```