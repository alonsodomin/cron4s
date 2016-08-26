package cron4s.ext

import cron4s.expr.CronExpr
import cron4s.types.Predicate

import scalaz.PlusEmpty

/**
  * Created by domingueza on 29/07/2016.
  */
abstract class ExtendedCronExpr[DateTime: DateTimeAdapter](expr: CronExpr) {

  private[this] def matches(implicit M: PlusEmpty[Predicate]): Predicate[DateTime] = {
    val reducer = new PredicateReducer[DateTime]
    reducer.run(expr)
  }

  def allOf: Predicate[DateTime] =
    matches(Predicate.conjunction)

  def anyOf: Predicate[DateTime] =
    matches(Predicate.disjunction)

  @inline
  def next(from: DateTime): Option[DateTime] = step(from, 1)

  @inline
  def previous(from: DateTime): Option[DateTime] = step(from, -1)

  def step(from: DateTime, amount: Int): Option[DateTime] = {
    val stepper = new Stepper[DateTime](from, amount)
    stepper.run(expr)
  }

}
