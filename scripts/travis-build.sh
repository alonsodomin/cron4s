#!/bin/bash

set -e

sbt_cmd="sbt ++$TRAVIS_SCALA_VERSION"

# Build & Test

build_js="$sbt_cmd validateJS"
build_jvm="$sbt_cmd validateJVM"
build_docs="$sbt_cmd docs/makeMicrosite"

build_all="$build_js && $build_jvm"

if [[ "$TRAVIS_SCALA_VERSION" != "2.13.0" ]]; then
    build_all="$build_all && $build_docs"
fi

eval $build_all

# Run Coverage Report

codacy="$sbt_cmd codacyCoverage"

if [[ ! -z "$CODACY_PROJECT_TOKEN" ]]; then
    eval $coverage
fi
bash <(curl -s https://codecov.io/bash)