---
layout: home
title:  "Home"
section: "home"
---

# Cron4s

[![Join the chat at https://gitter.im/alonsodomin/cron4s](https://badges.gitter.im/alonsodomin/cron4s.svg)](https://gitter.im/alonsodomin/cron4s?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![License](http://img.shields.io/:license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.15.svg)](https://www.scala-js.org)
[![Latest version](https://index.scala-lang.org/alonsodomin/cron4s/cron4s/latest.svg?color=green)](https://index.scala-lang.org/alonsodomin/cron4s)

[![Build Status](https://travis-ci.org/alonsodomin/cron4s.svg?branch=master)](https://travis-ci.org/alonsodomin/cron4s)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/7580c36bb6ec4f0888d6ac8213340f4d)](https://www.codacy.com/app/alonso-domin/cron4s?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=alonsodomin/cron4s&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/alonsodomin/cron4s/branch/master/graph/badge.svg)](https://codecov.io/gh/alonsodomin/cron4s)

Idiomatic Cron expression parsing in Scala and ScalaJS.

## Setup

To start using **cron4s** in your project just include the library as part of your dependencies:

```
libraryDependencies += "com.github.alonsodomin.cron4s" %% "cron4s-core" % "x.y.z"
```

Or in ScalaJS:

```
libraryDependencies += "com.github.alonsodomin.cron4s" %%% "cron4s-core" % "x.y.z"
```

**cron4s** is cross compiled for Scala 2.11 and Scala 2.12. Java 8 is required when using it in the JVM. 

## Limitations

Before delving into the user guide, is preferred to understand that the library at this given moment has some limitations
 in the expressions that is able to parse. These are the type of expressions that are not supported at the moment:
 
 * `L` alone in _day of month_ meaning _the last day of the month_
 * `nL` in _day of week_, being `n` a number between 0 and 6 meaning _the last day of week of a month_
 * `nW` in _day of month_, being `n` a number between 0 and 31 meaning _the nearest weekday (Monday-Friday) to the given month day_.
 * `n#m` in _day of week_, being `n` a number between 0 and 6 and `m` a number between 0 and 5 meaning _the mth day of the month_.

## License

Copyright 2016-2017 Antonio Alonso Dominguez

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
