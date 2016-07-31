package cron4s.java

import java.time.temporal.{ChronoField, Temporal, TemporalField}

import cron4s.CronField
import cron4s.expr._
import cron4s.ext._

/**
  * Created by domingueza on 29/07/2016.
  */
object time {
  import CronField._

  implicit def adapter[DT <: Temporal]: DateTimeAdapter[DT] = new DateTimeAdapter[DT] {

    private[this] def mapField(field: CronField): TemporalField = field match {
      case Minute     => ChronoField.MINUTE_OF_HOUR
      case Hour       => ChronoField.HOUR_OF_DAY
      case DayOfMonth => ChronoField.DAY_OF_MONTH
      case Month      => ChronoField.MONTH_OF_YEAR
      case DayOfWeek  => ChronoField.DAY_OF_WEEK
    }

    override def get[F <: CronField](dateTime: DT, field: F): Option[Int] = {
      val temporalField = mapField(field)

      val offset = if (field == DayOfWeek) -1 else 0
      if (!dateTime.isSupported(temporalField)) None
      else Some(dateTime.get(temporalField) + offset)
    }

    override def set[F <: CronField](dateTime: DT, field: F, value: Int): Option[DT] = {
      val temporalField = mapField(field)
      if (!dateTime.isSupported(temporalField)) None
      else Some(dateTime.`with`(temporalField, value.toLong).asInstanceOf[DT])
    }

  }

  implicit class Java8Expr[F <: CronField, DT <: Temporal](expr: Expr[F]) extends ExtendedExpr[F, DT](expr)
  implicit class Java8CronExpr[DT <: Temporal](expr: CronExpr) extends ExtendedCronExpr[DT](expr)

}
