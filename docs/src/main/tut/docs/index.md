---
layout: docs
title: "First Steps"
---

## First Steps

To start using **cron4s** in your project just include the library as part of your dependencies:

```
libraryDependencies += "com.github.alonsodomin.cron4s" %% "cron4s-core" % "x.y.z"
```

Or in ScalaJS:

```
libraryDependencies += "com.github.alonsodomin.cron4s" %%% "cron4s-core" % "x.y.z"
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

val parsed = Cron("10-35 2,4,6 * ? * *")
```

We will get an `Either[Error, CronExpr]`, the left side giving us an error description if the parsing
has failed. In the above example the expression parsed successfully but if we pass a wrong or invalid expression
we will get the actual reason for the failure in the left side of the `Either`:

```tut
val invalid = Cron("10-65 * * * * *")
```

```tut:invisible
assert(invalid.isLeft)
```

If we are not interested in the left side of the result (the error), we can easily convert it into an `Option[CronExpr]`:

```tut
import cats.syntax.either._

parsed.toOption
invalid.toOption
```

**_Note:_** _In Scala 2.12 you can avoid importing `cats.syntax.either._` to be able to make the conversion._

`Cron(expr)` is just a short-hand for `Cron.parse(expr)`. This object provides also with additional methods for parsing
that return different types. In the first place we have `Cron.tryParse(expr)` which will return a `Try[CronExpr]` instead:

```tut
val invalid = Cron.tryParse("10-65 * * * * *")
```

```tut:invisible
assert(invalid.isFailure)
```

And also `Cron.unsafeParse(expr)`, which will return a _naked_ `CronExpr` or happily blow-up with an exception
interrupting the execution. This is the most Java-friendly version of all of them and you should try to avoid using it
unless you are aware of the consequences (well, it also comes handy during a REPL session or to write this tutorial):

```tut:fail
Cron.unsafeParse("10-65 * * * * *")
```

#### Validation errors

The CRON expression will be validated right after parsing. Any error found during this stage will be returned
as well in the left side of the `Either` returned by the `Cron` constructor. For instance, the following expression
has a sequence that mixes elements that self-imply each other, which is invalid: 

```tut
val invalid = Cron("12,10-20 * * * * ?")
```

```tut:invisible
assert(invalid.isLeft)
```

In this case, the error type in the left side of the `Either` can be narrowed down to `InvalidCron`, which will give
a `NonEmptyList` with all the validation errors that the expression had. To demosrate this, here is an example:

```tut
val invalidCron: InvalidCron = invalid.left.get.asInstanceOf[InvalidCron]
println(invalidCron.reason.toList.mkString("\n"))
```

### The Cron4s AST

After successfully parsing a CRON expression, the `CronExpr` resulting type represents the previously
parsed expression as an AST, in which we can access all the expression fields individually:

```tut:invisible
val Right(cron) = parsed
```

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

Or by means of the `field` method in `CronExpr` and passing either the cron field type.
   
```tut
cron.field[CronField.Minute]
```

Some other basic operations at the `CronExpr` level are asking for the list of supported fields of the
actual value ranges for all the fields in the form of a map:

```tut
cron.supportedFields
cron.ranges
```

To convert an AST back into the original string expression we simply use the `toString` method:

```tut
cron.toString
```

### Sub-expressions

All the operations possible on a `CronExpr` are also possible in any of its subexpressions (either time or date) so
you can use them in exactly the same way. For example:

```tut
cron.supportedFields
cron.timePart.supportedFields
cron.datePart.supportedFields
```

`supportedFields` is not super-interesting at `CronExpr` (we expect it to support all the fields anyway) but when
is part of the sub-expressions gives us a more particular piece of information about the actual expression itself. The
`field` method is also interesting, it can return the field node expression given a specific field type:

```tut
cron.field[CronField.DayOfMonth]
cron.timePart.field[CronField.Hour]
cron.datePart.field[CronField.DayOfWeek]
```

It's important to note that when we pass a field type that is not supported by the given expression, we get a compile
 error:

```tut:fail
cron.timePart.field[CronField.DayOfMonth]
```

This is just a teaser, we will see much more interesting operations on cron expressions later but it's good to know
that all operations possible on a `CronExpr`, are also possible on it's subexpressions.

#### Field nodes

All field nodes have their own type, which is parameterized in the actual field type they operate on. We can
access that field type definition via the `unit` of field expression:

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

We can also obtain a field expression 

To obtain the string representation of individual fields we use the same `toString` method:

```tut
cron.seconds.toString
cron.field[CronField.Minute].toString
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

fixedSecond.implies(eachSecond)
fixedSecond.impliedBy(eachSecond)
eachSecond.implies(fixedSecond)

val minutesRange = BetweenNode[CronField.Minute](ConstNode(2), ConstNode(10))
val fixedMinute = ConstNode[CronField.Minute](7)

fixedMinute.implies(minutesRange)
fixedMinute.impliedBy(minutesRange)
```

These two operations allways hold the following property (it looks obvious, but it's important):

```tut
assert(minutesRange.implies(fixedMinute) == fixedMinute.impliedBy(minutesRange))
```

It's important to notice that when using either the `implies` or `impliedBy` operation, if the two nodes are not
parameterized for the same field type, the code won't compile:
 
```tut:fail
minutesRange.implies(eachSecond)
```

The error looks a bit scary, but in essence is saying to us that the `implies` method was expecting
any kind of expression as long as it was for the `Minute` field (expressed as `EE[cron4s.CronField.Minute]`).
