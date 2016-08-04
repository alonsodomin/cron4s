package cron4s.bench

import cron4s._
import org.openjdk.jmh.annotations._

@State(Scope.Benchmark)
class CronUnitIndexOfBenchmark {

  final val MinuteAtBeginning = 0
  final val MinuteInMiddle = 30
  final val MinuteAtEnd = 59

  @Benchmark
  def indexOfInUnit_beginning(): Option[Int] = {
    CronUnit.Minutes.indexOf(MinuteAtBeginning)
  }

  @Benchmark
  def indexOfInRange_beginning(): Int = {
    CronUnit.Minutes.range.indexOf(MinuteAtBeginning)
  }

  @Benchmark
  def indexOfInUnit_middle(): Option[Int] = {
    CronUnit.Minutes.indexOf(MinuteInMiddle)
  }

  @Benchmark
  def indexOfInRange_middle(): Int = {
    CronUnit.Minutes.range.indexOf(MinuteInMiddle)
  }

  @Benchmark
  def indexOfInUnit_end(): Option[Int] = {
    CronUnit.Minutes.indexOf(MinuteAtEnd)
  }

  @Benchmark
  def indexOfInRange_end(): Int = {
    CronUnit.Minutes.range.indexOf(MinuteAtEnd)
  }

}
