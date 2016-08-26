package cron4s.ext

import cron4s.CronField
import cron4s.expr.Expr
import cron4s.types._

/**
  * Created by alonsodomin on 31/07/2016.
  */
abstract class ExtendedExpr[E[_] <: Expr[F], F <: CronField, DateTime]
    (expr: E[F])
    (implicit adapter: DateTimeAdapter[DateTime], ev: IsFieldExpr[E, F]) {

  def matchesIn: Predicate[DateTime] = Predicate { dt =>
    val current = adapter.get(dt, expr.unit.field)
    current.map(expr.matches).getOrElse(false)
  }

  @inline
  def next(dateTime: DateTime): Option[DateTime] = step(dateTime, 1)

  @inline
  def previous(dateTime: DateTime): Option[DateTime] = step(dateTime, -1)

  def step(dateTime: DateTime, step: Int): Option[DateTime] = {
    for {
      current  <- adapter.get(dateTime, expr.unit.field)
      newValue <- expr.step(current, step).map(_._1)
      adjusted <- adapter.set(dateTime, expr.unit.field, newValue)
    } yield adjusted
  }

}
