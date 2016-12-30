---
layout: docs
title: "First Steps"
---

## First Steps

To start using **cron4s** in your project just include the library as part of your dependencies:

```
libraryDependencies += "com.github.alonsodomin.cron4s" %% "cron4s" % "x.y.z"
```

Or in ScalaJS:

```
libraryDependencies += "com.github.alonsodomin.cron4s" %%% "cron4s" % "x.y.z"
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

We will get an `Either[InvalidCron, CronExpr]`, the right side giving us an error description if the parsing
has failed. In the above example the expression parsed successfully but if we pass a wrong or invalid expression
we will get the actual reason for the failure in the left side of the `Either`:

```tut
val invalid = Cron("10-65 * * * * *")
```

```tut:invisible
assert(invalid.isLeft)
```

Assuming that we have successfully parsed an expression, we can extract it out of the `Either[..., ...]`
with following expression:

```tut
val cron = parsed.right.get
```

**_Note:_** _It is not recommended to use `.right.get` to extract values out of `Either[..., ...]`
types, we are doing it here as a means of simplifying the types just for the sake of the tutorial._ 

#### Validation errors

The CRON expression will be validated right after parsing all any error found during this stage will be returned
as well in the left side of the `Either` returned by the `Cron` constructor. For instance, the following expression
has a sequence that mixes elements that self-imply each other, which is invalid: 

```tut
val invalid = Cron("12,10-20 * * * * *")
```

```tut:invisible
assert(invalid.isLeft)
```

### The Cron4s AST

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

#### Field expressions

All field expressions have their own type, which is parameterized in the actual field type they
operate on. We can access that field type definition via the `unit` of field expression:

```tut
cron.seconds.unit.field
```

The expression unit can be used to give us information about what values are valid for that
specific field:

```tut
cron.seconds.unit.range
```

Which is different than the range of values accepted by the expression at that given field:

```tut
cron.seconds.range
```

We can also obtain a field expression by means of the `field` method in `CronExpr` and passing
either the cron field type or the cron unit.

```tut
cron.field[CronField.Minute]
cron.field(CronUnit.Minutes)
```

Other interesting operations are the ones that can be used to test if a given value matches the
field expression:

```tut
cron.seconds.matches(5)
cron.seconds.matches(15)
cron.minutes.matches(4)
cron.minutes.matches(5)
```

Or to test if a given field expression is implied by another one (that is also parameterized by
the same field type). To show this, let's work with some simple field expressions:

```tut
import cron4s.expr._

val eachSecond = EachNode[CronField.Second]
val fixedSecond = ConstNode[CronField.Second](30)

eachSecond.impliedBy(fixedSecond)
fixedSecond.impliedBy(eachSecond)

val minutesRange = BetweenNode[CronField.Minute](ConstNode(2), ConstNode(10))
val fixedMinute = ConstNode[CronField.Minute](7)

minutesRange.impliedBy(fixedMinute)
fixedMinute.impliedBy(minutesRange)
```

It's important to notice that when using the `impliedBy` operation, if the two expressions are not
parameterized for the same field type, the code won't compile:
 
```tut:fail
minutesRange.impliedBy(eachSecond)
```

The error looks a bit scary, but in essence is saying to us that the `impliedBy` method was expecting
any kind of expression as long as it was for the `Minute` field (expressed as `EE[cron4s.CronField.Minute]`).