#!/bin/bash

set -e

sbt_cmd="sbt ++$TRAVIS_SCALA_VERSION"

# Build & Test

build_js="$sbt_cmd validateJS"
build_jvm="$sbt_cmd coverage validateJVM"

build_all="$build_js && $build_jvm"

eval $build_all

# Run Coverage Report

report="$sbt_cmd coverageReport"
aggregate="$sbt_cmd coverageAggregate"
codacy="$sbt_cmd codacyCoverage"

if [[ ! -z "$CODACY_PROJECT_TOKEN" ]]; then
    coverage="$report && $aggregate && $codacy"
else
    coverage="$report && $aggregate"
fi

eval $coverage
bash <(curl -s https://codecov.io/bash)