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

  val eachExpr = EachExpr[CronField.Minute]
  val constExpr = ConstExpr[CronField.Minute](30)
  val betweenExpr = BetweenExpr(
    ConstExpr[CronField.Minute](CronUnit.Minutes.min),
    ConstExpr[CronField.Minute](CronUnit.Minutes.max)
  )
  val severalEnumeratedExpr = {
    val minutes = for {
      value <- CronUnit.Minutes.range
    } yield Coproduct[EnumExprAST[CronField.Minute]](ConstExpr[CronField.Minute](value))
    SeveralExpr(NonEmptyList(minutes.head, minutes.tail: _*))
  }
  val severalBetweenExpr: SeveralExpr[CronField.Minute] = {
    val betweenExpr: BetweenExpr[CronField.Minute] = BetweenExpr(
      ConstExpr[CronField.Minute](CronUnit.Minutes.min),
      ConstExpr[CronField.Minute](CronUnit.Minutes.max)
    )
    SeveralExpr(NonEmptyList(betweenExpr))
  }
  val everyEachExpr = EveryExpr(eachExpr, 1)

  @Benchmark
  def matchEachExpr(): Boolean = {
    EachExpr[CronField.Minute].matches(ValueToMatch)
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
