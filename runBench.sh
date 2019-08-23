#!/bin/sh

benchmark="*Benchmark"
if [[ -n "$1" ]]; then
  benchmark="$1Benchmark"
fi

sbt "bench/jmh:run -r 2 -i 10 -w 2 -wi 10 -f 1 -t 1 cron4s.bench.$benchmark"