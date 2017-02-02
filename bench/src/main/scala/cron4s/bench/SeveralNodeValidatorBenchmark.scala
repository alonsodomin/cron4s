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
class SeveralNodeValidatorBenchmark {
  import CronField._

  val _simpleValid = SeveralNode(ConstNode[Second](35))
  val _simpleInvalid = SeveralNode(ConstNode[Second](67))

  val _threeConstValid = SeveralNode(
    ConstNode[Second](4), ConstNode[Second](21), ConstNode[Second](54)
  )
  val _threeConstInvalid = SeveralNode(
    ConstNode[Second](-2), ConstNode[Second](60), ConstNode[Second](120)
  )

  val _rangeValid = SeveralNode(BetweenNode[Second](ConstNode(4), ConstNode(10)))
  val _rangeInvalid = SeveralNode(BetweenNode[Second](ConstNode(10), ConstNode(4)))
  val _rangeInvalidConst = SeveralNode(BetweenNode[Second](ConstNode(-6), ConstNode(61)))

  @Benchmark
  def simpleValid(): List[FieldError] = {
    NodeValidator[SeveralNode[Second]].validate(_simpleValid)
  }

  @Benchmark
  def simpleInvalid(): List[FieldError] = {
    NodeValidator[SeveralNode[Second]].validate(_simpleInvalid)
  }

  @Benchmark
  def threeConstValid(): List[FieldError] = {
    NodeValidator[SeveralNode[Second]].validate(_threeConstValid)
  }

  @Benchmark
  def threeConstInvalid(): List[FieldError] = {
    NodeValidator[SeveralNode[Second]].validate(_threeConstInvalid)
  }

  @Benchmark
  def rangeValid(): List[FieldError] = {
    NodeValidator[SeveralNode[Second]].validate(_rangeValid)
  }

  @Benchmark
  def rangeInvalid(): List[FieldError] = {
    NodeValidator[SeveralNode[Second]].validate(_rangeInvalid)
  }

  @Benchmark
  def rangeInvalidConst(): List[FieldError] = {
    NodeValidator[SeveralNode[Second]].validate(_rangeInvalidConst)
  }

}
