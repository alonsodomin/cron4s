package cron4s.expr

/**
  * Created by alonsodomin on 02/01/2016.
  */
final case class CronExpr(
    minutes: MinutesExpr,
    hours: HoursExpr,
    daysOfMonth: DaysOfMonthExpr,
    month: MonthsExpr,
    daysOfWeek: DaysOfWeekExpr
)
