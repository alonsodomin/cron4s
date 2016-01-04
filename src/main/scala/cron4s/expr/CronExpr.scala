package cron4s.expr

import java.time.LocalDateTime
import java.time.temporal.ChronoField

import CronField._
import cats.syntax.contravariant._
import cron4s.expr.Segment.{DaysOfMonth, Hours, Minutes}
import cron4s.matcher.Matcher

/**
  * Created by alonsodomin on 02/01/2016.
  */
case class CronExpr(minutes: Minutes, hours: Hours, daysOfMonth: DaysOfMonth, month: Segment.Months[_],
                    daysOfWeek: Segment.DaysOfWeeks[_]) {

  def matcher: Matcher[LocalDateTime] = Matcher { dt =>
    minutes.matcher(dt) && hours.matcher(dt) && daysOfMonth.matcher(dt) && month.matcher(dt) && daysOfWeek.matcher(dt)
  }

}
