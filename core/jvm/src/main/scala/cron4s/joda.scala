package cron4s

import cron4s.expr._
import cron4s.ext._

import org.joda.time.{DateTime, DateTimeFieldType}

/**
  * Created by domingueza on 29/07/2016.
  */
object joda {
  import CronField._

  implicit object JodaTimeAdapter extends DateTimeAdapter[DateTime] {

    private[this] def mapField[F <: CronField](field: F): DateTimeFieldType = field match {
      case Minute     => DateTimeFieldType.minuteOfHour()
      case Hour       => DateTimeFieldType.hourOfDay()
      case DayOfMonth => DateTimeFieldType.dayOfMonth()
      case Month      => DateTimeFieldType.monthOfYear()
      case DayOfWeek  => DateTimeFieldType.dayOfWeek()
    }

    override def get[F <: CronField](dateTime: DateTime, field: F): Option[Int] = {
      val jodaField = mapField(field)

      if (!dateTime.isSupported(jodaField)) None
      else Some(dateTime.get(jodaField))
    }

    override def set[F <: CronField](dateTime: DateTime, field: F, value: Int): Option[DateTime] = {
      val jodaField = mapField(field)
      if (!dateTime.isSupported(jodaField)) None
      else Some(dateTime.withField(jodaField, value))
    }
  }

  implicit class JodaCronExpr(expr: CronExpr) extends ExtendedCronExpr[DateTime](expr)
  implicit class JodaExpr[F <: CronField](expr: Expr[F]) extends ExtendedExpr[F, DateTime](expr)

}
