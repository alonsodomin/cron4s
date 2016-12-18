package cron4s.testkit.laws

import cron4s.CronField
import cron4s.spi.{DateTimeAdapter, ExprDateTimeOps}
import cron4s.types._

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ExprDateTimeLaws[E[_ <: CronField], F <: CronField, DateTime] {
  implicit def adapter: DateTimeAdapter[DateTime]
  implicit def eq: Equal[DateTime]
  implicit def ev: IsFieldExpr[E, F]

  def matchable(expr: E[F], dt: DateTime): Boolean = {
    val fieldVal = adapter.get(dt, ev.unit(expr).field)
    val exExpr = new ExprDateTimeOps[E, F, DateTime](expr, adapter, ev) {}
    exExpr.matchesIn(dt) === fieldVal.exists(ev.matches(expr)(_))
  }

  def forward(expr: E[F], from: DateTime): Boolean = {
    val exExpr = new ExprDateTimeOps[E, F, DateTime](expr, adapter, ev) {}
    exExpr.nextIn(from) === exExpr.stepIn(from, 1)
  }

  def backwards(expr: E[F], from: DateTime): Boolean = {
    val exExpr = new ExprDateTimeOps[E, F, DateTime](expr, adapter, ev) {}
    exExpr.prevIn(from) === exExpr.stepIn(from, -1)
  }

}

object ExprDateTimeLaws {

  def apply[E[_ <: CronField], F <: CronField, DateTime](implicit
      adapterEv: DateTimeAdapter[DateTime],
      eqEv: Equal[DateTime],
      exprEv: IsFieldExpr[E, F]
  ): ExprDateTimeLaws[E, F, DateTime] =
    new ExprDateTimeLaws[E, F, DateTime] {
      implicit val adapter = adapterEv
      implicit val eq = eqEv
      implicit val ev = exprEv
    }

}
