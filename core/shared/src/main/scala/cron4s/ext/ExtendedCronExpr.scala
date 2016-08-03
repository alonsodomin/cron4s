package cron4s.ext

import cron4s.expr.CronExpr
import cron4s.matcher.Matcher

import scalaz.PlusEmpty

/**
  * Created by domingueza on 29/07/2016.
  */
abstract class ExtendedCronExpr[DateTime: DateTimeAdapter](expr: CronExpr) {

  private[this] def matches(implicit M: PlusEmpty[Matcher]): Matcher[DateTime] = {
    val reducer = new MatcherReducer[DateTime]
    reducer.run(expr)
  }

  def allOf: Matcher[DateTime] =
    matches(Matcher.conjunction)

  def anyOf: Matcher[DateTime] =
    matches(Matcher.disjunction)

  @inline
  def next(from: DateTime): Option[DateTime] = step(from, 1)

  @inline
  def previous(from: DateTime): Option[DateTime] = step(from, -1)

  def step(from: DateTime, amount: Int): Option[DateTime] = {
    val stepper = new Stepper[DateTime](from, amount)
    stepper.run(expr)
  }

}
