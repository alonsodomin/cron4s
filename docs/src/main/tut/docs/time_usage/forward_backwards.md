---
layout: docs
title: "Forwards & Backwards in Time"
---

## Forwards & Backwards in Time

Matching is OK but it's not exactly what Cron expressions are made for. They have
been created to be able to calculate the following or previous moment in time to
a given one. To see is in action, let's start with our own basic imports:

```tut:silent
import java.time._
import cron4s._
import cron4s.japi.time._
```

And an already parsed CRON expression:

```tut
val Right(cron) = Cron("10-35 2,4,6 * * * *")
```

Now the `next` operation is able to return to us the next moment in time according
to the CRON expression:

```tut
val now = LocalDateTime.of(2016, 12, 1, 0, 4, 9)
cron.next(now)
```

And of course, we can also get the previous moment in time to a given one:

```tut
cron.previous(now)
```

If for some reason we do not want the next one, but the following to the next one,
then we could recursively invoke the `next` operation in any subsequent generated
time; or we can get it more efficiently using the operation `step` and telling it
how big is the step size that we want to make:

```tut
cron.step(now, 2)
```