#!/bin/bash

set -e

sbt_cmd="sbt ++$TRAVIS_SCALA_VERSION"

# Build & Test

build_js="$sbt_cmd validateJS"
build_jvm="$sbt_cmd validateJVM"

build_all="$build_js && $build_jvm"

eval $build_all

# Run Coverage Report

codacy="$sbt_cmd codacyCoverage"

if [[ ! -z "$CODACY_PROJECT_TOKEN" ]]; then
    eval $coverage
fi
bash <(curl -s https://codecov.io/bash)