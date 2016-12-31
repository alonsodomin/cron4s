package cron4s.expr

import cron4s.{CronField, generic}
import shapeless._

final case class DatePartExpr(
                               daysOfMonth: DaysOfMonthNode,
                               months: MonthsNode,
                               daysOfWeek: DaysOfWeekNode
  ) {

  private[cron4s] lazy val ast: DatePartAST = Generic[DatePartExpr].to(this)

  override def toString = ast.map(generic.ops.show).toList.mkString(" ")

}

final case class TimePartExpr(
                               seconds: SecondsNode,
                               minutes: MinutesNode,
                               hours: HoursNode
  ) {

  private[cron4s] lazy val ast: TimePartAST = Generic[TimePartExpr].to(this)

  override def toString = ast.map(generic.ops.show).toList.mkString(" ")

}