#!/bin/bash

set -e

git fetch --tags
sbt ci-release

if [ -n "$TRAVIS_TAG" ]; then
    echo "Publishing documentation..."
    sbt publishMicrosite
fi