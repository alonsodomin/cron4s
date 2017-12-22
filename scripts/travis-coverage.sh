#!/bin/bash

sbt_cmd="sbt ++$TRAVIS_SCALA_VERSION"

report="$sbt_cmd coverageReport"
aggregate="$sbt_cmd coverageAggregate"
codacy="$sbt_cmd codacyCoverage"

coverage="$report && $aggregate && $codacy"

eval $coverage
bash <(curl -s https://codecov.io/bash)