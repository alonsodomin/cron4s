# Cron4s

[![Build Status](https://travis-ci.org/alonsodomin/cron4s.svg?branch=master)](https://travis-ci.org/alonsodomin/cron4s)
[![Scala.js](http://scala-js.org/assets/badges/scalajs-0.6.8.svg)](http://scala-js.org)
[![License](http://img.shields.io/:license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.alonsodomin.cron4s/cron4s_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.alonsodomin.cron4s/cron4s_2.11)

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/7580c36bb6ec4f0888d6ac8213340f4d)](https://www.codacy.com/app/alonso-domin/cron4s?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=alonsodomin/cron4s&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/alonsodomin/cron4s/branch/master/graph/badge.svg)](https://codecov.io/gh/alonsodomin/cron4s)

Idiomatic Cron expression parsing in Scala and ScalaJS.

## Getting started

To start using **cron4s** in your project just include the library as part of your dependencies:

```
libraryDependencies += "com.github.alonsodomin.cron4s" %% "cron4s-core" % "x.y.z"
```

Or in ScalaJS:

```
libraryDependencies += "com.github.alonsodomin.cron4s" %%% "cron4s-core" % "x.y.z"
```

## How to Use

To start with, we will need to parse a cron expression into a type that  we can work with:

```
scala> import cron4s._
import cron4s._

scala> val parsed = Cron("10-35 2,4,6 * * *")
parsed: Either[cron4s.ParseError,cron4s.expr.CronExpr] = Right(10-35 2,4,6 * * *)
```

We will get an `Either[ParseError, CronExpr]`, the right side giving us an error description if
the parsing has failed. Assuming that we have successfully parsed an expression, there
are two different things that we can do with it: Using it as a matcher against _DateTime_ objects
and obtaining the next or previous _DateTime_ to a given one according to the parsed expression.

Now, there are many flavours of _DateTime_ objects in either the JVM or in JS land. **cron4s**
supports a few out of the box and it also makes it easy to add your own types. Every single
supported library out of box, or _adapted_ to work with **cron4s** will support the same
operations with the same semantics. So let's take a look at what are those operations

### Matching against time

To be able to show examples of how to use **cron4s**, we are going to base them in the brand
new Java Time API added to Java 8 as is one of the bests (if not the best) date & time
libraries available. To use the built-in support for it we need to import a couple of packages:

```
import java.time._
import cron4s.japi.time._
```

Now we can perform 2 types of matches against any object that extends `java.time.Temporal`:

```
scala> val cron: CronExpr = ...
cron: cron4s.expr.CronExpr = ...

scala> val now = LocalDateTime.now
now: java.time.LocalDateTime = 2016-08-03T18:35:45.982

scala> cron.allOf(now)
res0: Boolean = false

scala> cron.anyOf(now)
res1: Boolean = true
```

`allOf` will evaluate all the fields of the Cron expression against all the fields
of the _DateTime_ object and it will return `false` if there is any one that does
not match the sub-expression corresponding to that field. In the other hand, `anyOf`
will return `true` if there is at least one field in the Cron expression that matches
it corresponding field.

### Forwarding or Rewinding time

Matching is OK but it's not exactly what Cron expressions are made for. They have
been created to be able to calculate the following moment in time to a given one,
and for doing that we have the operation `next`:

```
scala> cron.next(now)
res2: Option[java.time.LocalDateTime] = Some(2016-08-04T02:10:45.982)
```

And of course, we can also get the previous moment in time to a given one:

```
scala> cron.previous(now)
res3: Option[java.time.LocalDateTime] = Some(2016-08-03T18:34:45.982)
```

If for some reason we do not want the next one, but the following to the next one,
then we could recursively invoke the `next` operation in any subsequent generated
time; or we can get it more efficiently using the operation `step` and telling it
how big is the step size that we want to make:

```
scala> cron.step(now, 2)
res4: Option[java.time.LocalDateTime] = Some(2016-08-04T02:11:45.982)
```

## Built-in library support

These are the libraries that are currently supported:

### At the JVM

 * Java Time API (JSR-310): Package `cron4s.japi.time`
 * Joda Time: Package `cron4s.joda`

### ScalaJS

 * JavaScript `Date` API: Package `cron4s.js`
 * Java Time API (JSR-310): Package `cron4s.japi.threetenbp`. _Support is provided via [scala-java-time](https://github.com/soc/scala-java-time), which can also be used in the JVM. The two implementations for JSR-310 will be merged into one once this library reaches a final release._

## Status

At the moment the library should be considered as experimental and not ready
for production yet. APIs might change and there are some [issues](https://github.com/alonsodomin/cron4s/issues) to get sorted
out before a formal release is done.

## License

Copyright 2016 Antonio Alonso Dominguez

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
