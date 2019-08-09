#!/bin/bash

set -e

git fetch --tags
sbt ci-release

if [ -n "$TRAVIS_TAG" ] && [ "$TRAVIS_SCALA_VERSION" = "2.12.8" ]; then
    echo "Publishing documentation..."
    sbt docs/publishMicrosite
fi