#!/bin/bash

set -e

sbt ci-release

if [ "$TRAVIS_BRANCH" = "master" ] && [ ! -z "$TRAVIS_TAG" ] && [ "$TRAVIS_SCALA_VERSION" = "2.12.8" ]; then
    echo "Publishing documentation..."
    sbt docs/publishMicrosite
fi