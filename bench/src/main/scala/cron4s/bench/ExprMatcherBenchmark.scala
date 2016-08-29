package cron4s.bench

import cron4s._
import cron4s.expr._

import org.openjdk.jmh.annotations._

import scalaz.NonEmptyList

/**
  * Created by alonsodomin on 03/08/2016.
  */
@State(Scope.Benchmark)
class ExprMatcherBenchmark {

  final val ValueToMatch = 30

  val anyExpr = AnyExpr[CronField.Minute.type]
  val constExpr = ConstExpr(CronField.Minute, 30)
  val betweenExpr = BetweenExpr(
    ConstExpr(CronField.Minute, CronUnit.Minutes.min),
    ConstExpr(CronField.Minute, CronUnit.Minutes.max)
  )
  val severalEnumeratedExpr = {
    val minutes: Seq[EnumerableExpr[CronField.Minute.type]] = for {
      value <- CronUnit.Minutes.range
    } yield ConstExpr(CronField.Minute, value)
    SeveralExpr(NonEmptyList(minutes.head, minutes.tail: _*))
  }
  val severalBetweenExpr = {
    val betweenExpr: EnumerableExpr[CronField.Minute.type] = BetweenExpr(
      ConstExpr(CronField.Minute, CronUnit.Minutes.min),
      ConstExpr(CronField.Minute, CronUnit.Minutes.max)
    )
    SeveralExpr(NonEmptyList(betweenExpr))
  }
  val everyAnyExpr = EveryExpr(anyExpr, 1)

  @Benchmark
  def matchAnyExpr(): Boolean = {
    AnyExpr[CronField.Minute.type].matches(ValueToMatch)
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
  def matchEveryAnyExpr(): Boolean = {
    everyAnyExpr.matches(ValueToMatch)
  }

}
