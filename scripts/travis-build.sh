#!/bin/bash

set -e

sbt_cmd="sbt ++$TRAVIS_SCALA_VERSION"

# Build cache commands

pre_build="$sbt_cmd preBuild"
post_build="$sbt_cmd postBuild"

# Build & Test

build_js="$sbt_cmd validateJS"
build_jvm="$sbt_cmd validateJVM"
build_docs="$sbt_cmd makeMicrosite"

build_all="$pre_build && $build_js && $build_jvm && $build_docs && $post_build"

eval $build_all

# Run Coverage Report

codacy="$sbt_cmd codacyCoverage"

if [[ ! -z "$CODACY_PROJECT_TOKEN" ]]; then
    eval $coverage
fi
bash <(curl -s https://codecov.io/bash)