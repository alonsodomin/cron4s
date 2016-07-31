package cron4s.ext

import cron4s.CronField
import cron4s.expr.Expr
import cron4s.matcher.Matcher

/**
  * Created by alonsodomin on 31/07/2016.
  */
abstract class ExtendedExpr[F <: CronField, DateTime: DateTimeAdapter](expr: Expr[F]) {

  def matches: Matcher[DateTime] = Matcher { dt =>
    val adapter = implicitly[DateTimeAdapter[DateTime]]
    val current = adapter.extract(dt, expr.unit.field)
    current.map(expr.matches).getOrElse(false)
  }

  @inline
  def next(dateTime: DateTime): Option[DateTime] = step(dateTime, 1)

  @inline
  def previous(dateTime: DateTime): Option[DateTime] = step(dateTime, -1)

  def step(dateTime: DateTime, step: Int): Option[DateTime] = {
    val adapter = implicitly[DateTimeAdapter[DateTime]]
    for {
      current  <- adapter.extract(dateTime, expr.unit.field)
      newValue <- expr.step(current, step).map(_._1)
      adjusted <- adapter.adjust(dateTime, expr.unit.field, newValue)
    } yield adjusted
  }

}
