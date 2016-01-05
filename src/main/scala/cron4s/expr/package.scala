package cron4s

import cats.data.Xor

/**
  * Created by alonsodomin on 04/01/2016.
  */
package object expr {

  type MinutesPart     = Part[CronField.Minute.type]
  type HoursPart       = Part[CronField.Hour.type]
  type DaysOfMonthPart = Part[CronField.DayOfMonth.type]
  type MonthsPart      = Part[CronField.Month.type]
  type DaysOfWeekPart  = Part[CronField.DayOfWeek.type]

}
