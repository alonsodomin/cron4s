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
import org.openjdk.jmh.annotations._

import scala.annotation.nowarn

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class ParserBenchmark {
  @Param(
    Array(
      "10-35 2,4,6 * ? * *",
      "* 5,10,15,20,25,30,35,40,45,50,55/2 * ? * mon-fri",
      "10-65 * * * * *",
      "* */10 5-10 ? * mon-fri",
      "*/30 10,20,40 5-15,25-35/4 ? 1,3,7,oct-dec sun"
    )
  )
  var cronString: String = _

  @nowarn("cat=deprecation")
  @Benchmark
  def parserCombinators() = parsing.Parser.parse(cronString)

  @Benchmark
  def attoParser() = atto.Parser.parse(cronString)
}
