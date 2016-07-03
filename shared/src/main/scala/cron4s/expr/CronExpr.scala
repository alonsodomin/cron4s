package cron4s.expr

/**
  * Created by alonsodomin on 02/01/2016.
  */
case class CronExpr(
    minutes: MinutesPart,
    hours: HoursPart,
    daysOfMonth: DaysOfMonthPart,
    month: MonthsPart,
    daysOfWeek: DaysOfWeekPart)
