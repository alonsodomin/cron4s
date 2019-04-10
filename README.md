# Cron4s

[![Join the chat at https://gitter.im/alonsodomin/cron4s](https://badges.gitter.im/alonsodomin/cron4s.svg)](https://gitter.im/alonsodomin/cron4s?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/alonsodomin/cron4s.svg?branch=master)](https://travis-ci.org/alonsodomin/cron4s)
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.17.svg)](https://www.scala-js.org)
[![License](http://img.shields.io/:license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Latest version](https://index.scala-lang.org/alonsodomin/cron4s/cron4s/latest.svg?color=green)](https://index.scala-lang.org/alonsodomin/cron4s)

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/7580c36bb6ec4f0888d6ac8213340f4d)](https://www.codacy.com/app/alonso-domin/cron4s?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=alonsodomin/cron4s&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/alonsodomin/cron4s/branch/master/graph/badge.svg)](https://codecov.io/gh/alonsodomin/cron4s)

Idiomatic Cron expression parsing in Scala and ScalaJS.

## Documentation

Please, take a look at the [user guide](https://alonsodomin.github.io/cron4s) for instructions on how to use this library. The remaining of this section will be focused on information for other developers.

### Building

`cron4s` is built using [SBT](https://www.scala-sbt.org):

```bash
git clone https://github.com/alonsodomin/cron4s
cd cron4s
sbt test
```

The user documentation is built/generated using [`sbt-microsites`](https://47deg.github.io/sbt-microsites/) and requires [Jekyll](https://jekyllrb.com) to be installed in the local machine. To generate and browse the latest docs from master follow these steps from the project's root folder:

```bash
sbt makeMicrosite
cd docs/target/site
jekyll serve
```

Then make your browser point to <http://127.0.0.1:4000/cron4s> and that's all.

### Cross building

`cron4s` is cross-built and published for several versions of Scala. The different set of versions are configured in the [`.travis.yml`](https://github.com/alonsodomin/cron4s/blob/master/.travis.yml) file in the `scala` section. For example:

```yaml
scala:
  - 2.11.12
  - 2.12.8
```

Supporting additional Scala versions is a matter of adding a new version to that section and then make the codebase compile against it whilst preserving backwards compatibility. The easiest way to check that compatibility is triggering a cross build with SBT:

```bash
sbt +test
```

### Repository Automation

There is some sort of automation configured in this repository to ease the management of some _tedious or repetitive_ tasks:

* **Version upgrades**: The [Scala Steward](https://github.com/fthomas/scala-steward) will be producing pull requests with version upgrades of this library dependencies.
* **Auto-merge**: [Mergify](https://mergify.io) is installed in this repo to do auto-merge of pull requests that meet a given criteria. This said criteria can be modified in the [.mergify](https://github.com/alonsodomin/cron4s/blob/master/.mergify.yml) at the project's root folder.
* **Travis Release**: [Travis CI](https://travis-ci.org/alonsodomin/cron4s) has been configured, not just to build the project, but also to be able to release to Maven Central with minimal user interaction. Pushing a tag in the repo with the version number will sign and publish such a release in Maven Central, non-tagged commits to `master` will be published as `SNAPSHOT`.  

## Related Projects

This is a non-exhaustive list of other open source projects and libraries that use or are somewhat related to Cron4s. If you have a library that depends on Cron4s you could add it [here](https://github.com/alonsodomin/cron4s/edit/master/README.md):

 * [fs2-cron](https://github.com/fthomas/fs2-cron): Create pure functional streams that emit elements based on a given cron schedule.
 * [pureconfig](https://github.com/pureconfig/pureconfig): Cron expression parsing support in configuration files.

## License

Copyright 2017 Antonio Alonso Dominguez

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
