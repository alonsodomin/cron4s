#!/bin/bash

sbt_cmd="sbt ++$TRAVIS_SCALA_VERSION"

build_js="$sbt_cmd validateJVM"
build_jvm="$sbt_cmd coverage validateJVM"

build_all="$build_js && $build_jvm"

eval $build_all