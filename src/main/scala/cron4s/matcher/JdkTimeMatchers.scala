package cron4s.matcher

import java.time.LocalDateTime
import java.time.temporal.{ChronoField, TemporalField}

import cats.std.list._
import cron4s.expr._

/**
  * Created by alonsodomin on 04/01/2016.
  */
object JdkTimeMatchers {
  import CronField._

  def cronField2TemporalField(field: CronField): TemporalField = field match {
    case Minute     => ChronoField.MINUTE_OF_HOUR
    case Hour       => ChronoField.HOUR_OF_DAY
    case DayOfMonth => ChronoField.DAY_OF_MONTH
    case Month      => ChronoField.MONTH_OF_YEAR
    case DayOfWeek  => ChronoField.DAY_OF_WEEK
  }

  implicit def temporalAccessor(field: CronField, accessor: LocalDateTime): Int = {
    val offset = if (field == DayOfWeek) -1 else 0
    accessor.get(cronField2TemporalField(field)) + offset
  }

  implicit class CronExprMatcher(cronExpr: CronExpr) {

    def matcher: Matcher[LocalDateTime] = {
      import Matcher._
      val parts = List(
        cronExpr.minutes.matcherFor[LocalDateTime],
        cronExpr.hours.matcherFor[LocalDateTime],
        cronExpr.daysOfMonth.matcherFor[LocalDateTime],
        cronExpr.month.matcherFor[LocalDateTime],
        cronExpr.daysOfWeek.matcherFor[LocalDateTime]
      )

      not (forall(parts))
    }

  }

}
