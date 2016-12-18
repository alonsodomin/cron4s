package cron4s.expr

import cron4s.CronField

import shapeless._

final case class DatePartExpr(
    daysOfMonth: DaysOfMonthAST,
    months: MonthsAST,
    daysOfWeek: DaysOfWeekAST
  ) {

  private[cron4s] lazy val ast: DatePartAST = Generic[DatePartExpr].to(this)

  override def toString = ast.map(cron4s.generic.show).toList.mkString(" ")

}

final case class TimePartExpr(
    seconds: SecondsAST,
    minutes: MinutesAST,
    hours: HoursAST
  ) {

  private[cron4s] lazy val ast: TimePartAST = Generic[TimePartExpr].to(this)

  override def toString = ast.map(cron4s.generic.show).toList.mkString(" ")

}