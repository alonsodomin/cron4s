---
layout: docs
title: "AST"
section: "userguide"
---

```scala mdoc:invisible
import cron4s._
```

## The Cron4s AST

After successfully parsing a CRON expression, the `CronExpr` resulting type represents the previously
parsed expression as an AST, in which we can access all the expression fields individually. Let's start by declaring a cron expression:

```scala mdoc
val cron = Cron.unsafeParse("10-35 2,4,6 * ? * *")
```

And now with that out of the way, we can access the different components of the AST using the field accessors:

```scala mdoc
cron.seconds
cron.minutes
cron.months
```

We can also take the date or time parts only of the expression using either `timePart` or `datePart`:

```scala mdoc
val time = cron.timePart
val date = cron.datePart
```

And similarly as with the main expression type, we can get individual fields out of the
date and time sub expressions:
 
```scala mdoc
time.seconds
time.minutes

date.daysOfMonth
```

Or by means of the `field` method in `CronExpr` and passing the cron field type.
   
```scala mdoc
cron.field[CronField.Minute]
```

Some other basic operations at the `CronExpr` level are asking for the list of supported fields of the
actual value ranges for all the fields in the form of a map:

```scala mdoc
cron.supportedFields
cron.ranges
```

To convert an AST back into the original string expression we simply use the `toString` method:

```scala mdoc
cron.toString
```

## Sub-expressions

All the operations possible on a `CronExpr` are also possible in any of its subexpressions (either time or date) so
you can use them in exactly the same way. For example:

```scala mdoc
cron.supportedFields
cron.timePart.supportedFields
cron.datePart.supportedFields
```

`supportedFields` is not super-interesting at `CronExpr` (we expect it to support all the fields anyway) but when
is part of the sub-expressions gives us a more particular piece of information about the actual expression itself. The
`field` method is also interesting, it can return the field node expression given a specific field type:

```scala mdoc
cron.field[CronField.DayOfMonth]
cron.timePart.field[CronField.Hour]
cron.datePart.field[CronField.DayOfWeek]
```

It's important to note that when we pass a field type that is not supported by the given expression, we get a compile
 error:

```scala mdoc:fail
cron.timePart.field[CronField.DayOfMonth]
```

This is just a teaser, we will see much more interesting operations on cron expressions later but it's good to know
that all operations possible on a `CronExpr`, are also possible on it's subexpressions.

### Field nodes

All field nodes have their own type, which is parameterized in the actual field type they operate on. We can
access that field type definition via the `unit` of field expression:

```scala mdoc
cron.seconds.unit.field
```

The expression unit can be used to give us information about what values are valid for that
specific field:

```scala mdoc
cron.seconds.unit.range
```

Which is different than the range of values accepted by the expression at that given field:

```scala mdoc
cron.seconds.range
```

We can also obtain a field expression 

To obtain the string representation of individual fields we use the same `toString` method:

```scala mdoc
cron.seconds.toString
cron.field[CronField.Minute].toString
```

Other interesting operations are the ones that can be used to test if a given value matches the
field expression:

```scala mdoc
cron.seconds.matches(5)
cron.seconds.matches(15)
cron.minutes.matches(4)
cron.minutes.matches(5)
```

Or to test if a given field expression is implied by another one (that is also parameterized by
the same field type). To show this, let's work with some simple field expressions:

```scala mdoc
import cron4s.expr._

val eachSecond = EachNode[CronField.Second]
val fixedSecond = ConstNode[CronField.Second](30)

fixedSecond.implies(eachSecond)
fixedSecond.impliedBy(eachSecond)
eachSecond.implies(fixedSecond)

val minutesRange = BetweenNode[CronField.Minute](ConstNode(2), ConstNode(10))
val fixedMinute = ConstNode[CronField.Minute](7)

fixedMinute.implies(minutesRange)
fixedMinute.impliedBy(minutesRange)
```

These two operations allways hold the following property (it looks obvious, but it's important):

```scala mdoc
assert(minutesRange.implies(fixedMinute) == fixedMinute.impliedBy(minutesRange))
```

It's important to notice that when using either the `implies` or `impliedBy` operation, if the two nodes are not
parameterized by the same field type, the code won't compile:

```scala //mdoc:fail disabled because it breaks mdoc
minutesRange.implies(eachSecond)
```

The error looks a bit scary, but in essence is saying to us that the `implies` method was expecting
any kind of expression as long as it was for the `Minute` field (expressed as `EE[cron4s.CronField.Minute]`).