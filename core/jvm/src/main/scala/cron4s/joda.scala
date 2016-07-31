package cron4s

import cron4s.expr._
import cron4s.ext._

import org.joda.time.DateTimeFieldType
import org.joda.time.base.AbstractInstant

import scala.util.Try

/**
  * Created by domingueza on 29/07/2016.
  */
object joda {
  import CronField._

  implicit def adapter[DT <: AbstractInstant]: DateTimeAdapter[DT] = new DateTimeAdapter[DT] {

    private[this] def mapField[F <: CronField](field: F): DateTimeFieldType = field match {
      case Minute     => DateTimeFieldType.minuteOfHour()
      case Hour       => DateTimeFieldType.hourOfDay()
      case DayOfMonth => DateTimeFieldType.dayOfMonth()
      case Month      => DateTimeFieldType.monthOfYear()
      case DayOfWeek  => DateTimeFieldType.dayOfWeek()
    }

    override def get[F <: CronField](dateTime: DT, field: F): Option[Int] =
      Try(dateTime.get(mapField(field))).toOption

    override def set[F <: CronField](dateTime: DT, field: F, value: Int): Option[DT] = {
      Try {
        val mutableDate = dateTime.toMutableDateTime
        mutableDate.set(mapField(field), value)
        mutableDate.asInstanceOf[DT]
      } toOption
    }
  }

  implicit class JodaCronExpr[DT <: AbstractInstant](expr: CronExpr) extends ExtendedCronExpr[DT](expr)
  implicit class JodaExpr[F <: CronField, DT <: AbstractInstant](expr: Expr[F]) extends ExtendedExpr[F, DT](expr)

}
