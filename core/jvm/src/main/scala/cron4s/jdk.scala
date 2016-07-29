package cron4s

import java.time.temporal.{ChronoField, TemporalAccessor, TemporalField}

import cron4s.expr._

/**
  * Created by domingueza on 29/07/2016.
  */
object jdk {
  import CronField._

  implicit def fieldExtractor[T <: TemporalAccessor](field: CronField, accessor: T): Option[Int] = {
    val temporalField: TemporalField = field match {
      case Minute     => ChronoField.MINUTE_OF_HOUR
      case Hour       => ChronoField.HOUR_OF_DAY
      case DayOfMonth => ChronoField.DAY_OF_MONTH
      case Month      => ChronoField.MONTH_OF_YEAR
      case DayOfWeek  => ChronoField.DAY_OF_WEEK
    }

    val offset = if (field == DayOfWeek) -1 else 0
    if (!accessor.isSupported(temporalField)) None
    else Some(accessor.get(temporalField) + offset)
  }

  implicit class RichCronExpr(expr: CronExpr) extends RichCronExprBase[TemporalAccessor](expr)

}
