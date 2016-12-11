package cron4s.spi

import cron4s.CronField
import cron4s.expr.Expr
import cron4s.types._

/**
  * Created by alonsodomin on 31/07/2016.
  */
abstract class ExtendedExpr[E[_ <: CronField] <: Expr[_], F <: CronField, DateTime]
    (private[spi] val underlying: E[F])
    (implicit adapter: DateTimeAdapter[DateTime], ev: IsFieldExpr[E, F]) {

  def matchesIn: Predicate[DateTime] = Predicate { dt =>
    val current = adapter.get(dt, underlying.asInstanceOf[Expr[F]].unit.field)
    current.map(ev.matches(underlying)).getOrElse(false)
  }

  @inline
  def next(dateTime: DateTime): Option[DateTime] = step(dateTime, 1)

  @inline
  def prev(dateTime: DateTime): Option[DateTime] = step(dateTime, -1)

  def step(dateTime: DateTime, step: Int): Option[DateTime] = {
    for {
      current  <- adapter.get(dateTime, underlying.asInstanceOf[Expr[F]].unit.field)
      newValue <- ev.step(underlying)(current, step).map(_._1)
      adjusted <- adapter.set(dateTime, underlying.asInstanceOf[Expr[F]].unit.field, newValue)
    } yield adjusted
  }

}
