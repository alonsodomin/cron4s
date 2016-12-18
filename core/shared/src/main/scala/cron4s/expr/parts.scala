package cron4s.expr

import cron4s.CronField

import shapeless._

final case class DatePartExpr(
    daysOfMonth: DaysOfMonthAST,
    months: MonthsAST,
    daysOfWeek: DaysOfWeekAST
  ) {

  private[cron4s] lazy val repr: DatePartAST = Generic[DatePartExpr].to(this)

  override def toString = repr.map(cron4s.util.show).toList.mkString(" ")

}

final case class TimePartExpr(
    seconds: SecondsAST,
    minutes: MinutesAST,
    hours: HoursAST
  ) {

  private[cron4s] lazy val repr: TimePartAST = Generic[TimePartExpr].to(this)

  override def toString = repr.map(cron4s.util.show).toList.mkString(" ")

}