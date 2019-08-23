---
layout: docs
title: "Forwards & Backwards in Time"
section: "userguide"
---

## Forwards & Backwards in Time

Matching is OK but it's not exactly what Cron expressions are made for. They have
been created to be able to calculate the following or previous moment in time to
a given one. To see is in action, let's start with our own basic imports:

```tut:silent
import java.time._
import cron4s._
import cron4s.lib.javatime._
```

And an already parsed CRON expression:

```tut
val cron = Cron.unsafeParse("10-35 2,4,6 * ? * *")
```

Now the `next` operation is able to return to us the next moment in time according
to the CRON expression:

```tut
val now = LocalDateTime.of(2016, 12, 1, 0, 4, 34)
cron.next(now)
```

And of course, we can also get the previous moment in time to a given one:

```tut
cron.prev(now)
```

Let's try this with the sub-expressions too:

```tut
cron.datePart.next(now)
cron.timePart.prev(now)
```

If for some reason we do not want the next one, but the following to the next one,
then we could recursively invoke the `next` operation in any subsequent generated
time; or we can get it more efficiently using the operation `step` and telling it
how big is the step size that we want to make:

```tut
cron.step(now, 2)
cron.timePart.step(now, 4)
cron.datePart.step(now, -3)
```

### Individual fields

The same type of operations are also available on the individual fields of the CRON
expression:

```tut
cron.seconds.nextIn(now)
cron.minutes.prevIn(now)
```

### Why so many `Option[...]`?

As you must have noticed, all the methods that operate on date times have an `Option[...]`
 return type. The reason for that type is to be able to express the fact that sometimes
 you can not obtain a meaningful result out of the standard operations. Let's see it in an
 example:
 
```tut
val today = LocalDate.of(2017, 5, 12)
cron.next(today)
```

So in this case the `next` method returns `None` instead of giving us the next datetime to
 the given local date according to the cron expression. The reason for this is because
 Cron4s can not give you a `LocalDate` **according the full cron expression** (since it can't
 express the time values with it). The `next` and `prev` methods have been designed to **reply
 with the same type that has been given as a parameter** (if possible), so in this case `None`
 is being used to signal the fact that it's not possible.
 
The are two different ways to workaround this, one of them is using a subexpression such that
 we can get our desired `LocalDate`:
 
```tut
cron.datePart.next(today)
```

Now we get a `LocalDate` but this may not still be what we want, since all the constrains
 defined for the time fields are being ignored.
 
If what we are looking for is a `LocalDateTime` relative to the `LocalDate` we can easily get
 one with the following code:
 
```tut
cron.next(today.atStartOfDay())
```

The same applies when working with `LocalTime` instances:

```tut
val before = LocalTime.of(0, 4, 34)
cron.next(before)
cron.timePart.next(before)
cron.next(before.atDate(today))
```

And of course, same happens when dealing with the individual fields of the cron expression:

```tut
cron.seconds.nextIn(today)
cron.months.nextIn(today)
cron.minutes.nextIn(before)
cron.daysOfMonth.nextIn(before)
```
