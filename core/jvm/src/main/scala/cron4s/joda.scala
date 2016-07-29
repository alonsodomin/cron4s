package cron4s

import org.joda.time.{DateTimeFieldType, ReadableInstant}
import cron4s.expr._

import scala.util.Try

/**
  * Created by domingueza on 29/07/2016.
  */
object joda {
  import CronField._

  implicit def fieldExtractor[A <: ReadableInstant](field: CronField, instant: A): Option[Int] = {
    val instantField = field match {
      case Minute     => DateTimeFieldType.minuteOfHour()
      case Hour       => DateTimeFieldType.hourOfDay()
      case DayOfMonth => DateTimeFieldType.dayOfMonth()
      case Month      => DateTimeFieldType.monthOfYear()
      case DayOfWeek  => DateTimeFieldType.dayOfWeek()
    }

    Try(instant.get(instantField)).toOption
  }

  implicit class RichCronExpr(expr: CronExpr) extends RichCronExprBase[ReadableInstant](expr)

}
