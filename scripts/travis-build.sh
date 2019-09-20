#!/bin/bash

set -e

sbt_cmd="sbt ++$TRAVIS_SCALA_VERSION!"

# Build & Test

build_all="$sbt_cmd rebuild"
eval $build_all

# Run Coverage Report

codacy="$sbt_cmd codacyCoverage"

if [[ ! -z "$CODACY_PROJECT_TOKEN" ]]; then
    eval $coverage
fi
bash <(curl -s https://codecov.io/bash)
