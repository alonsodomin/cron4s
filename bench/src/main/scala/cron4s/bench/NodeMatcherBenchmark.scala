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

import org.openjdk.jmh.annotations._


/**
  * Sample run
  *    sbt "bench/jmh:run -r 2 -i 20 -w 2 -wi 20 -f 1 -t 1 cron4s.bench.NodeMatcherBenchmark"
  *
  * Which means "20 iterations" of "2 seconds" each, "20 warm-up
  * iterations" of "2 seconds" each, "1 fork", "1 thread".  Please note
  * that benchmarks should be usually executed at least in 10
  * iterations (as a rule of thumb), but the more is better.
  */
@State(Scope.Benchmark)
class NodeMatcherBenchmark {

  final val ValueToMatch = 30

  val eachNode = EachNode[CronField.Minute]
  val constNode = ConstNode[CronField.Minute](30)
  val betweenNode = BetweenNode(
    ConstNode[CronField.Minute](CronUnit.Minutes.min),
    ConstNode[CronField.Minute](CronUnit.Minutes.max)
  )
  val severalEnumeratedNode = {
    val minutes = for {
      value <- CronUnit.Minutes.range
    } yield const2Enumerable(ConstNode[CronField.Minute](value))
    SeveralNode(minutes.head, minutes.tail: _*)
  }
  val severalBetweenNode: SeveralNode[CronField.Minute] = {
    val betweenNode: BetweenNode[CronField.Minute] = BetweenNode(
      ConstNode[CronField.Minute](CronUnit.Minutes.min),
      ConstNode[CronField.Minute](CronUnit.Minutes.max)
    )
    SeveralNode(betweenNode)
  }
  val everyEachNode = EveryNode(eachNode, 1)

  @Benchmark
  def matchEachNode(): Boolean = {
    EachNode[CronField.Minute].matches(ValueToMatch)
  }

  @Benchmark
  def matchConstNode(): Boolean = {
    constNode.matches(ValueToMatch)
  }

  @Benchmark
  def matchBetweenNode(): Boolean = {
    betweenNode.matches(ValueToMatch)
  }

  @Benchmark
  def matchSeveralNodeEnumerated(): Boolean = {
    severalEnumeratedNode.matches(ValueToMatch)
  }

  @Benchmark
  def matchSeveralNodeBetween(): Boolean = {
    severalBetweenNode.matches(ValueToMatch)
  }

  @Benchmark
  def matchEveryEachNode(): Boolean = {
    everyEachNode.matches(ValueToMatch)
  }

}
