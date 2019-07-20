# Cron4s Change Log

## Unreleased changes

New Features:

* [#142](https://github.com/alonsodomin/cron4s/pull/142): Add support for Scala 2.13 (and drop support for Scala 2.11)
* [#149](https://github.com/alonsodomin/cron4s/pull/149): Add extension module for [doobie](https://tpolecat.github.io/doobie/).
  Find documentation about it in the [doobie extension](https://alonsodomin.github.io/cron4s/extensions/doobie.html) docs.
* [#138](https://github.com/alonsodomin/cron4s/pull/138): Rewrite parser using Scala Parser Combinators instead of FastParse.

Version upgrades:

* Scala 2.12.8 / Scala 2.13.0
* ScalaJS 0.6.28
* Cats 2.0.0-M4
* Decline 0.7.0-M0
* Circe 0.12.0-M4
* Scala Java Time 2.0.0-RC3

## 0.5.0

This version adds some features and upgrades some of the core dependencies. This version **is not binary compatible**
with the 0.4.x series.

Bug Fixes:

* Fixed a parser inconsistency error in which the string representation of a given cron AST could be parsed into a
  different (although equivalent) one. This was fixed as part of the work for [#127](https://github.com/alonsodomin/cron4s/pull/127).

New features:

* [#125](https://github.com/alonsodomin/cron4s/pull/125): Add extension module for [decline](http://ben.kirw.in/decline/).
  Find documentation about it in the [extensions](https://alonsodomin.github.io/cron4s/extensions/decline.html) docs.
* [#127](https://github.com/alonsodomin/cron4s/pull/127): Add extension module for [circe](http://circe.io).
  Find documentation about it in the [extensions](https://alonsodomin.github.io/cron4s/extensions/circe.html) docs.
* [#126](https://github.com/alonsodomin/cron4s/pull/126): Add a compile-time string interpolator.

Version upgrades:

* Scala 2.11.12 / Scala 2.12.8
* ScalaJS 0.6.26
* Cats 1.6.0
* Scala Java Time 2.0.0-RC1

## 0.4.4

This is a maintenance release over 0.4.3:

Changes:

* [#94](https://github.com/alonsodomin/cron4s/pull/94): The signature for the `get` method in the `IsDateTime` typeclass
  has been changed to capture errors when getting the value of a given field.

Version upgrades:

* Scala 2.11.12 / Scala 2.12.4
* ScalaJS 0.6.21
* Cats 1.0.1

## 0.4.3

This is a maintenance release over 0.4.2:

Version upgrades:

* Scala 2.11.11 / Scala 2.12.4
* ScalaJS 0.6.20
* Cats 1.0.0-RC1
* FastParse 1.0.0

## 0.4.2

This a maintenance release over 0.4.1:

Version Upgrades:

* Scala 2.11.11 / Scala 2.12.3
* ScalaJS 0.6.19
* Cats 1.0.0-MF
* FastParse 0.4.4

## 0.4.1

This is a bug fix release over 0.4.0:

Bug Fixes:

* [#73](https://github.com/alonsodomin/cron4s/issues/73)/[#80](https://github.com/alonsodomin/cron4s/issues/73):
  Rolling an update to the _day of month_ field could cause setting an invalid date, which will eventually return
  a `None` result from the `next`/`prev` methods. This case is now handled and will propagate a carry over value to
  the _month_ field whenever it happens.

Changes:

* [#81](https://github.com/alonsodomin/cron4s/pull/81): The signature for the `set` method in the `IsDateTime` typeclass
  has been changed to capture errors when setting the values.
* [#78](https://github.com/alonsodomin/cron4s/pull/78): Upgrading the Scala version to 2.12.2 means that case classes
  do not support anymore having implicit parameter lists. This means that all node class implementations under package
  `cron4s.expr` have been rewritten using normal Scala classes instead of case classes. This means that they can not
  be used anymore in pattern matching and that binary compatibility in their case has been broken.

Version Upgrades:

* Scala: 2.11.11 / 2.12.2
* ScalaJS: 0.6.18
* Scala Java Time: 2.0.0-M12

Please, check migration document for instructions on how to migrate your code to the last version:
 [Migration to 0.4.1](https://alonsodomin.github.io/cron4s/docs/migration/0_4_0.html)

## 0.4.0

This the 4th release of Cron4s, aimed at a more complete implementation of the _CRON Spec_, easier (if possible) to
 use, and with important bug fixes.

Improvements:

* [#58](https://github.com/alonsodomin/cron4s/pull/58): CRON expressions now support `?` symbols in both _day of month_ 
  and _day of week_ fields. This also means that now expressions must use that symbol in one of those two fields to be valid.
* Additional parse operations where added to the `Cron` entry point: `parse`, `tryParse` and `unsafeParse`. Good-old
  `Cron("...")` syntax is still supported and it be the preferred one as it's the most concise one.
* [#70](https://github.com/alonsodomin/cron4s/pull/70): Improved parser error messages.
* [#60](https://github.com/alonsodomin/cron4s/pull/60): ScalaJS & ScalaJVM support for JSR-310 (aka Java 8 Time) has
  been unified in the `cron4s.lib.javatime` package.
* [#67](https://github.com/alonsodomin/cron4s/pull/67): Added module with support for MomentJS.
* [#66](https://github.com/alonsodomin/cron4s/pull/66): `Cron` entry point is exported as a CommonJS module in ScalaJS.

Bug Fixes:

* [#59](https://github.com/alonsodomin/cron4s/issues/59): Carry over from fields _month_ and _day of week_ wasn't
  properly applied to the datetime.
* [#56](https://github.com/alonsodomin/cron4s/issues/56): The effect of updating a date-time field propagates the
  effect to the predecessor fields, no matter the direction of this effect (forward or backwards in time).

Changes:

* [#63](https://github.com/alonsodomin/cron4s/pull/63): [Scalaz](http://scalaz.org) has been replaced by [Cats](http://www.typelevel.org/cats).
* [#67](https://github.com/alonsodomin/cron4s/pull/67): Joda Time support has been moved to it's own module.
* [#61](https://github.com/alonsodomin/cron4s/pull/61): Field expression selection by field type is now fully type-checked
  during compilation.

Version Upgrades:

* ScalaJS: 0.6.15
* Cats: 0.9.0
* Scala Java Time: 2.0.0-M10

Please, check migration document for instructions on how to migrate your code to the last version:
 [Migration to 0.4.0](https://alonsodomin.github.io/cron4s/docs/migration/0_4_0.html) 

## 0.3.1

[cron4s](https://alonsodomin.github.io/cron4s) 0.3.1 is an incremental update with the following changes:

* [#39](https://github.com/alonsodomin/cron4s/issues/39): Fixed type signature in summon method for type class `DateTimeCron`
* [#41](https://github.com/alonsodomin/cron4s/issues/41): Support for Joda's partial date times: LocalDate, LocalTime, LocalDateTime
* [#45](https://github.com/alonsodomin/cron4s/pull/45): Do no reference tests modules in parent POM as a compile dependency
* [#52](https://github.com/alonsodomin/cron4s/pull/52): Preserve _step inertia_ when calculating a next or previous date, initially reported as in [#50](https://github.com/alonsodomin/cron4s/issues/50)
* [#51](https://github.com/alonsodomin/cron4s/issues/51): Reset milliseconds in DateTime return values

## 0.3.0

[cron4s](https://alonsodomin.github.io/cron4s) 0.3.0 has an overhaul of its internals from 0.2.1. This release is not
source compatible with previous ones as it has several API breaking changes which will make users have to update
their codebases to be able to compile with it. The list of changes in this release are:

* Updated ScalaJS to 0.6.14
* Support for Scala 2.12 [(#22)](https://github.com/alonsodomin/cron4s/pull/22)
* Re-organized the built-in datetime library support under the `cron4s.lib` package.
* `cron4s.expr.AnyExpr` has been renamed to `cron4s.expr.EachNode` to properly reflect the semantics of the `*` symbol in CRON expressions. [(#22)](https://github.com/alonsodomin/cron4s/pull/22)
* Parser implementation has been rewritten using [FastParse](http://www.lihaoyi.com/fastparse/) [(#22)](https://github.com/alonsodomin/cron4s/pull/22)
* Full type signatures in the AST are now preserved after parsing the expression [(#24)](https://github.com/alonsodomin/cron4s/pull/24)
* The parsed expression AST is not fully validated and any error will result returning those errors instead of the actual expression object.
* Fixed inconsistencies when stepping over enumerated values of the different expression ranges [(#28)](https://github.com/alonsodomin/cron4s/pull/28)
* All expression and nodes now implement the `scalaz.Show` typeclass to consistently return the actual CRON string representation.
* Common datetime operations (matching and stepping) are now supported in subexpressions (date or time only)
* Providing support for custom date time libraries has been greatly simplified and [full documented](https://alonsodomin.github.io/cron4s/docs/custom_datetime.html) [(#37)](https://github.com/alonsodomin/cron4s/pull/37)

A [migration guide](https://alonsodomin.github.io/cron4s/docs/migration/0_3_0.html) has been published for anyone that would like to update.

## 0.2.1

cron4s 0.2.1 is a maintenance release over 0.2.0. The
list of changes in this release are:

* Documentation has been improved published as a [microsite](https://alonsodomin.github.io/cron4s)
* `timePart` and `datePart` fields in `CronExpr` expose field based accessors.
* `repr` accessor in `CronExpr` is now private

## 0.2.0

cron4s 0.2.0 is an API improvement release over the previous 0.1.0. The
list of changes in this release are:

* Deeper Typeclass based design which has helped to make a more concise and uniform implementation.
* Added a `Seconds` field to the cron expression and parser.
* Added a human-friendly `toString` implementation to all the expressions and cron units.
* Dropped support for `java.util.Calendar` as it is impossible to treat it as an immutable type and yields weird results.
* Improved parser error messages, including the position at which the parser stopped.
* Made `CronExpr` a proper case class with its own attributes to ease using it with serializers. Access to the `HList` is still granted with the same attribute names.
* Deprecated the `cron4s.matcher.Matcher` and replaced it by `cron4s.types.Predicate`
* Added a testkit module, used to test the library and useful to test user extensions.

## 0.1.0

`cron4s` 0.1.0 is a preview release of a fully Scala idiomatic CRON expression parser that can be used in the JVM or in ScalaJS. cron4s has been designed with extensibility in mind, meaning that it's not tied to an specific date-time library/implementation, but provides support for many instead.

Example:

```scala
import cron4s._
import java.time._
import cron4s.japi.time._

val Right(cron) = Cron("10-35 2,4,6 * * *")
val now = LocalDateTime.now
val nextDate = cron.next(now)
```
