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

  implicit def temporalAccessor(field: Month.type, accessor: LocalDateTime): String =
    accessor.get(cronField2TemporalField(field)).toString

  implicit def temporalAccessor(field: DayOfWeek.type, accessor: LocalDateTime): String =
    accessor.get(cronField2TemporalField(field)).toString

}
