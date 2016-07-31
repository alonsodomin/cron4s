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

  implicit object Adapter extends DateTimeAdapter[AbstractInstant] {

    private[this] def mapField[F <: CronField](field: F): DateTimeFieldType = field match {
      case Minute     => DateTimeFieldType.minuteOfHour()
      case Hour       => DateTimeFieldType.hourOfDay()
      case DayOfMonth => DateTimeFieldType.dayOfMonth()
      case Month      => DateTimeFieldType.monthOfYear()
      case DayOfWeek  => DateTimeFieldType.dayOfWeek()
    }

    override def extract[F <: CronField](dateTime: AbstractInstant, field: F): Option[Int] =
      Try(dateTime.get(mapField(field))).toOption

    override def adjust[F <: CronField](dateTime: AbstractInstant, field: F, value: Int): Option[AbstractInstant] = {
      Try {
        val mutableDate = dateTime.toMutableDateTime
        mutableDate.set(mapField(field), value)
        mutableDate
      } toOption
    }
  }

  implicit class JodaCronExpr(expr: CronExpr) extends ExtendedCronExpr[AbstractInstant](expr)
  implicit class JodaExpr[F <: CronField](expr: Expr[F]) extends ExtendedExpr[F, AbstractInstant](expr)

}
