#!/bin/sh

sbt "bench/jmh:run -r 2 -i 10 -w 2 -wi 20 -f 1 -t 1 cron4s.bench.*Benchmark"