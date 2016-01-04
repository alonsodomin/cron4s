package cron4s.matcher

import java.time.LocalDateTime
import java.time.temporal.{ChronoField, TemporalField, TemporalAccessor}

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

  implicit def temporalAccessor(field: CronField, accessor: LocalDateTime): Int =
    accessor.get(cronField2TemporalField(field))

  implicit class CronExprMatcher(cronExpr: CronExpr) {

    def matcher: Matcher[LocalDateTime] = Matcher { dt =>
      cronExpr.minutes.matcherFor[LocalDateTime].matches(dt) &&
        cronExpr.hours.matcherFor[LocalDateTime].matches(dt) &&
        cronExpr.daysOfMonth.matcherFor[LocalDateTime].matches(dt) &&
        cronExpr.month.matcherFor[LocalDateTime].matches(dt) &&
        cronExpr.daysOfWeek.matcherFor[LocalDateTime].matches(dt)
    }

  }

}
