package cron4s.testkit.laws

import cron4s.CronField
import cron4s.expr._
import cron4s.ext.{DateTimeAdapter, ExtendedExpr}
import cron4s.types._

import scalaz.Scalaz._
import scalaz._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ExtendedExprLaws[E[_ <: CronField] <: Expr[_], F <: CronField, DateTime] {
  implicit def adapter: DateTimeAdapter[DateTime]
  implicit def eq: Equal[DateTime]
  implicit def ev: IsFieldExpr[E, F]

  def matchable(expr: E[F], dt: DateTime): Boolean = {
    val fieldVal = adapter.get(dt, expr.asInstanceOf[Expr[F]].unit.field)
    val exExpr = new ExtendedExpr[E, F, DateTime](expr) {}
    exExpr.matchesIn(dt) === fieldVal.exists(ev.matches(expr)(_))
  }

  def forward(expr: E[F], from: DateTime): Boolean = {
    val exExpr = new ExtendedExpr[E, F, DateTime](expr) {}
    exExpr.next(from) === exExpr.step(from, 1)
  }

  def backwards(expr: E[F], from: DateTime): Boolean = {
    val exExpr = new ExtendedExpr[E, F, DateTime](expr) {}
    exExpr.prev(from) === exExpr.step(from, -1)
  }

}

object ExtendedExprLaws {

  def apply[E[_ <: CronField] <: Expr[_], F <: CronField, DateTime](implicit
      adapterEv: DateTimeAdapter[DateTime],
      eqEv: Equal[DateTime],
      exprEv: IsFieldExpr[E, F]
  ): ExtendedExprLaws[E, F, DateTime] =
    new ExtendedExprLaws[E, F, DateTime] {
      implicit def adapter = adapterEv
      implicit def eq = eqEv
      implicit def ev = exprEv
    }

}
