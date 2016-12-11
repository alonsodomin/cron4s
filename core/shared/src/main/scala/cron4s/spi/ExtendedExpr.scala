package cron4s.spi

import cron4s.CronField
import cron4s.expr.Expr
import cron4s.types._

/**
  * Base abstraction used to provide support for date-time libraries at field expression level
  *
  * @author Antonio Alonso Dominguez
  */
abstract class ExtendedExpr[E[_ <: CronField] <: Expr[_], F <: CronField, DateTime]
    (private[spi] val underlying: E[F])
    (implicit adapter: DateTimeAdapter[DateTime], ev: IsFieldExpr[E, F]) {

  /**
    * Tests if this field expressions matches in the given date-time
    *
    * @return true if there is a field in this date-time that matches this expression
    */
  def matchesIn: Predicate[DateTime] = Predicate { dt =>
    val current = adapter.get(dt, underlying.asInstanceOf[Expr[F]].unit.field)
    current.map(ev.matches(underlying)).getOrElse(false)
  }

  /**
    * Calculates the next date-time to a given one considering only the field
    * represented by this expression.
    *
    * @param dateTime date-time used as a reference
    * @return the next date-time
    */
  @inline
  def next(dateTime: DateTime): Option[DateTime] = step(dateTime, 1)

  /**
    * Calculates the previous date-time to a given one considering only the field
    * represented by this expression.
    *
    * @param dateTime date-time used as a reference
    * @return the next date-time
    */
  @inline
  def prev(dateTime: DateTime): Option[DateTime] = step(dateTime, -1)

  /**
    * Calculates a date-time that is in either the past or the future relative
    * to a given one, a delta amount and considering only the field represented
    * by this expression.
    *
    * @param dateTime date-time used as a reference
    * @param step step size
    * @return a date-time that is an amount of given steps from the given one
    */
  def step(dateTime: DateTime, step: Int): Option[DateTime] = {
    for {
      current  <- adapter.get(dateTime, underlying.asInstanceOf[Expr[F]].unit.field)
      newValue <- ev.step(underlying)(current, step).map(_._1)
      adjusted <- adapter.set(dateTime, underlying.asInstanceOf[Expr[F]].unit.field, newValue)
    } yield adjusted
  }

}
