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
