package cron4s.bench

import cron4s._
import cron4s.expr._

import org.openjdk.jmh.annotations._

import shapeless.Coproduct

import scalaz.NonEmptyList

/**
  * Sample run
  *    sbt "bench/jmh:run -r 2 -i 20 -w 2 -wi 20 -f 1 -t 1 cron4s.bench.ExprMatcherBenchmark"
  *
  * Which means "20 iterations" of "2 seconds" each, "20 warm-up
  * iterations" of "2 seconds" each, "1 fork", "1 thread".  Please note
  * that benchmarks should be usually executed at least in 10
  * iterations (as a rule of thumb), but the more is better.
  */
@State(Scope.Benchmark)
class ExprMatcherBenchmark {

  final val ValueToMatch = 30

  val eachExpr = EachNode[CronField.Minute]
  val constExpr = ConstNode[CronField.Minute](30)
  val betweenExpr = BetweenNode(
    ConstNode[CronField.Minute](CronUnit.Minutes.min),
    ConstNode[CronField.Minute](CronUnit.Minutes.max)
  )
  val severalEnumeratedExpr = {
    val minutes = for {
      value <- CronUnit.Minutes.range
    } yield Coproduct[SeveralMemberNode[CronField.Minute]](ConstNode[CronField.Minute](value))
    SeveralNode(NonEmptyList(minutes.head, minutes.tail: _*))
  }
  val severalBetweenExpr: SeveralNode[CronField.Minute] = {
    val betweenExpr: BetweenNode[CronField.Minute] = BetweenNode(
      ConstNode[CronField.Minute](CronUnit.Minutes.min),
      ConstNode[CronField.Minute](CronUnit.Minutes.max)
    )
    SeveralNode(NonEmptyList(betweenExpr))
  }
  val everyEachExpr = EveryNode(eachExpr, 1)

  @Benchmark
  def matchEachExpr(): Boolean = {
    EachNode[CronField.Minute].matches(ValueToMatch)
  }

  @Benchmark
  def matchConstExpr(): Boolean = {
    constExpr.matches(ValueToMatch)
  }

  @Benchmark
  def matchBetweenExpr(): Boolean = {
    betweenExpr.matches(ValueToMatch)
  }

  @Benchmark
  def matchSeveralExprEnumerated(): Boolean = {
    severalEnumeratedExpr.matches(ValueToMatch)
  }

  @Benchmark
  def matchSeveralExprBetween(): Boolean = {
    severalBetweenExpr.matches(ValueToMatch)
  }

  @Benchmark
  def matchEveryEachExpr(): Boolean = {
    everyEachExpr.matches(ValueToMatch)
  }

}
