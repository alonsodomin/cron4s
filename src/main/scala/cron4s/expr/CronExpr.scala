package cron4s.expr

import java.time.LocalDateTime
import java.time.temporal.ChronoField

import CronField._
import cats.syntax.contravariant._
import cron4s.expr.Segment.{DaysOfMonth, Hours, Minutes}
import cron4s.matcher._

/**
  * Created by alonsodomin on 02/01/2016.
  */
case class CronExpr(minutes: MinutesPart, hours: HoursPart, daysOfMonth: DaysOfMonthPart, month: MonthsPart,
                    daysOfWeek: DaysOfWeekPart) {

  import JdkTimeMatchers._

  def matcherFor: Matcher[LocalDateTime] = Matcher { dt =>
    minutes.matcherFor[LocalDateTime].apply(dt) &&
    hours.matcherFor[LocalDateTime].apply(dt) &&
    daysOfMonth.matcherFor[LocalDateTime].apply(dt) &&
    month.matcherFor[LocalDateTime].apply(dt) &&
    daysOfWeek.matcherFor[LocalDateTime].apply(dt)
  }

}
