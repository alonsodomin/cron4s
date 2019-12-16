/*
 * Copyright 2017 Antonio Alonso Dominguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cron4s.bench

import java.util.concurrent.TimeUnit

import cron4s._
import cron4s.expr._
import cron4s.validation.NodeValidator

import org.openjdk.jmh.annotations._

/**
  * sbt "bench/jmh:run -r 2 -i 20 -w 2 -wi 20 -f 1 -t 1 cron4s.bench.SeveralNodeValidatorBenchmark"
  *
  * Created by alonsodomin on 02/02/2017.
  */
@State(Scope.Benchmark)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class SeveralNodeValidatorBenchmark {
  import CronField._

  val _simpleValid   = SeveralNode(ConstNode[Second](35), ConstNode[Second](40))
  val _simpleInvalid = SeveralNode(ConstNode[Second](61), ConstNode[Second](67))

  val _threeConstValid = SeveralNode(
    ConstNode[Second](4),
    ConstNode[Second](21),
    ConstNode[Second](54)
  )
  val _threeConstInvalid = SeveralNode(
    ConstNode[Second](-2),
    ConstNode[Second](60),
    ConstNode[Second](120)
  )

  val _rangeValid = SeveralNode(
    ConstNode[Second](0),
    BetweenNode[Second](ConstNode(4), ConstNode(10))
  )
  val _rangeInvalid = SeveralNode(
    ConstNode[Second](0),
    BetweenNode[Second](ConstNode(10), ConstNode(4))
  )
  val _rangeInvalidConst = SeveralNode(
    ConstNode[Second](0),
    BetweenNode[Second](ConstNode(-6), ConstNode(61))
  )

  val _constImpliedByRange = SeveralNode(
    ConstNode[Second](23),
    BetweenNode[Second](ConstNode(10), ConstNode(15)),
    BetweenNode[Second](ConstNode(20), ConstNode(30))
  )
  val _overlappingRanges = SeveralNode(
    BetweenNode[Second](ConstNode(17), ConstNode(30)),
    BetweenNode[Second](ConstNode(25), ConstNode(50))
  )

  @Benchmark
  def simpleValid(): List[InvalidField] =
    NodeValidator[SeveralNode[Second]].validate(_simpleValid)

  @Benchmark
  def simpleInvalid(): List[InvalidField] =
    NodeValidator[SeveralNode[Second]].validate(_simpleInvalid)

  @Benchmark
  def threeConstValid(): List[InvalidField] =
    NodeValidator[SeveralNode[Second]].validate(_threeConstValid)

  @Benchmark
  def threeConstInvalid(): List[InvalidField] =
    NodeValidator[SeveralNode[Second]].validate(_threeConstInvalid)

  @Benchmark
  def rangeValid(): List[InvalidField] =
    NodeValidator[SeveralNode[Second]].validate(_rangeValid)

  @Benchmark
  def rangeInvalid(): List[InvalidField] =
    NodeValidator[SeveralNode[Second]].validate(_rangeInvalid)

  @Benchmark
  def rangeInvalidConst(): List[InvalidField] =
    NodeValidator[SeveralNode[Second]].validate(_rangeInvalidConst)

  @Benchmark
  def constImpliedByRange(): List[InvalidField] =
    NodeValidator[SeveralNode[Second]].validate(_constImpliedByRange)

  @Benchmark
  def overlappingRanges(): List[InvalidField] =
    NodeValidator[SeveralNode[Second]].validate(_overlappingRanges)
}
