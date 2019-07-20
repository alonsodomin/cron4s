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

import org.openjdk.jmh.annotations._

@State(Scope.Thread)
class ParserBenchmark {

  @Param(
    Array(
      "10-35 2,4,6 * ? * *",
      "* 5,10,15,20,25,30,35,40,45,50,55/2 * ? * mon-fri",
      "10-65 * * * * *",
      "* */10 5-10 ? * mon-fri"
    )
  )
  var cronString: String = _

  @Benchmark
  def parserCombinators() = parsing.parse(cronString)

  @Benchmark
  def attoParser() = atto.parse(cronString)

}
