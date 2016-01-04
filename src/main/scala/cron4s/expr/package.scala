package cron4s

import cats.data.Xor

/**
  * Created by alonsodomin on 04/01/2016.
  */
package object expr {

  type CronVal         = Int Xor String

  type MinutesPart     = Part[Int, CronField.Minute.type]
  type HoursPart       = Part[Int, CronField.Hour.type]
  type DaysOfMonthPart = Part[Int, CronField.DayOfMonth.type]
  type MonthsPart      = Part[_, CronField.Month.type]
  type DaysOfWeekPart  = Part[_, CronField.DayOfWeek.type]

}
