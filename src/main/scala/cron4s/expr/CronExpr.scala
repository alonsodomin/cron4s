package cron4s.expr

import CronField._

/**
  * Created by alonsodomin on 02/01/2016.
  */
case class CronExpr(minutes: Part[Int, Minute.type], hours: Part[Int, Hour.type],
                    daysOfMonth: Part[Int, DayOfMonth.type], month: Part[_, Month.type],
                    daysOfWeek: Part[_, DayOfWeek.type])
