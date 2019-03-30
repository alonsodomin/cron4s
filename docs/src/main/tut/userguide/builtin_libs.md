---
layout: docs
title: "Built-in libs"
section: "userguide"
---

Cron4s provides support out of box for Java 8 Time in its `cron4s-core` module (even in ScalaJS). The integration is
 available at `cron4s.lib.javatime` as many examples in this documentation have already shown. In ScalaJS, there is
 an additional `cron4s.lib.js` package which provides integration with JavaScript's `Date` object.

Support for other libraries is provided via extension modules as follows:

## Joda Time

**JVM Only**

Integration with Joda Time is possible by including the `cron4s-joda` module among your dependencies:

```
libraryDependencies += "com.github.alonsodomin.cron4s" %% "cron4s-joda" % "x.y.z"
```

After that, importing the package `cron4s.lib.joda` should be enough to use your Cron expressions against instances
 of Joda's `DateTime`, `LocalDateTime`, `LocalDate` and `LocalTime`.

## MomentJS

**JS Only**

To be able to use Cron4s with MomentJS you need to include the `cron4s-momentjs` module among your dependencies:

```
libraryDependencies += "com.github.alonsodomin.cron4s" %%% "cron4s-momentjs" % "x.y.z"
```

The relevant integration bridge is at package `cron4s.lib.momentjs`.