package cron4s.bench

import cron4s.expr._
import org.openjdk.jmh.annotations._

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
    val minutes = for {
      value <- CronUnit.Minutes.range
    } yield ConstExpr(CronField.Minute, value)
    SeveralExpr(minutes.head, minutes.tail: _*)
  }
  val severalBetweenExpr = SeveralExpr(BetweenExpr(
    ConstExpr(CronField.Minute, CronUnit.Minutes.min),
    ConstExpr(CronField.Minute, CronUnit.Minutes.max)
  ))
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
