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