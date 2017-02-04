#!/bin/bash

sbt_cmd="sbt ++$TRAVIS_SCALA_VERSION"

report="$sbt_cmd coverageReport"
aggregate="$sbt_cmd coverageAggregate"
codacy="$sbt_cmd codacyCoverage"
upload_report="bash <(curl -s https://codecov.io/bash)"

coverage="$report && $aggregate && $codacy && $upload_report"

eval $coverage