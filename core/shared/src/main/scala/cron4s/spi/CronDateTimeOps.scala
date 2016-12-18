package cron4s.spi

import cron4s.expr.CronExpr
import cron4s.types.Predicate

import scalaz.PlusEmpty

/**
  * Base abstraction used to provide support for date-time libraries.
  *
  * @author Antonio Alonso Dominguez
  */
abstract class CronDateTimeOps[DateTime: DateTimeAdapter](expr: CronExpr) {

  private[this] def matches(implicit M: PlusEmpty[Predicate]): Predicate[DateTime] = {
    val reducer = new PredicateReducer[DateTime]
    reducer.run(expr)
  }

  /**
    * Matches all the CRON expression fields against a given date-time
    *
    * @return true if all fields match, false otherwise
    */
  def allOf: Predicate[DateTime] =
    matches(Predicate.conjunction)

  /**
    * Tests whether some of the CRON expression fields matches against a given
    * date-time
    *
    * @return true if any of the fields matches, false otherwise
    */
  def anyOf: Predicate[DateTime] =
    matches(Predicate.disjunction)

  /**
    * Calculates the next date-time to a given one according to this expression
    *
    * @param from date-time used as a reference
    * @return next date-time to a given one according to this expression
    */
  @inline
  def next(from: DateTime): Option[DateTime] = step(from, 1)

  /**
    * Calculates the previous date-time to a given one according to this expression
    *
    * @param from date-time used as a reference
    * @return previous date-time to a given one according to this expression
    */
  @inline
  def prev(from: DateTime): Option[DateTime] = step(from, -1)

  /**
    * Calculates a date-time that is in either the past or the future relative
    * to a given one and a delta amount.
    *
    * @param from date-time used as a reference
    * @param amount delta position from the given date-time. Positive values
    *               represent the future, negative values the past
    * @return a date-time that is an amount of given steps from the given one
    */
  def step(from: DateTime, amount: Int): Option[DateTime] = {
    val stepper = new Stepper[DateTime](from, amount)
    stepper.run(expr)
  }

}
