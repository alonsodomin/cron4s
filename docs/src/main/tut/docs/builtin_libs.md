---
layout: docs
title: "Built-in libs"
---

## Built-in libs

These are the libraries that are currently supported:

### At the JVM

 * Java Time API (JSR-310): Package `cron4s.lib.javatime`
 * Joda Time: Package `cron4s.lib.joda`

### ScalaJS

 * JavaScript `Date` API: Package `cron4s.lib.js`
 * Java Time API: Package `cron4s.lib.javatime`. _Support is provided via [scala-java-time](https://github.com/cquiroz/scala-java-time) 
 and requires declaring an explicit dependency in client code._