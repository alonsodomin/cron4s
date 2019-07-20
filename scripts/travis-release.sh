#!/bin/bash

set -e

sbt ci-release

if [ "$TRAVIS_BRANCH" = "master" ] && [ ! -z "$TRAVIS_TAG" ]; then
    echo "Publishing documentation..."
    sbt docs/publishMicrosite
fi