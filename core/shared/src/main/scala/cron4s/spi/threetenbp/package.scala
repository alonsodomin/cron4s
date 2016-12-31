package cron4s.spi

import cron4s.CronField
import cron4s.CronField._
import cron4s.expr.CronExpr
import cron4s.types.Expr

import org.threeten.bp.temporal.{ChronoField, Temporal, TemporalField}
import org.threeten.bp.{LocalDateTime, ZonedDateTime}

import scalaz.Equal

/**
  * Created by alonsodomin on 11/12/2016.
  */
package object threetenbp {
  implicit val localDateTimeInstance = Equal.equalA[LocalDateTime]
  implicit val zonedDateTimeInstnce = Equal.equalA[ZonedDateTime]

  implicit def jsr310Adapter[DT <: Temporal]: DateTimeAdapter[DT] = new DateTimeAdapter[DT] {

    private[this] def mapField(field: CronField): TemporalField = field match {
      case Second     => ChronoField.SECOND_OF_MINUTE
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

      val offset = if (field == DayOfWeek) 1 else 0
      if (!dateTime.isSupported(temporalField)) None
      else Some(dateTime.`with`(temporalField, value.toLong + offset).asInstanceOf[DT])
    }

  }

  implicit class JSR310CronExpr[DT <: Temporal](expr: CronExpr) extends CronDateTimeOps[DT](expr)
  implicit class JSR310Node[E[_ <: CronField], F <: CronField, DT <: Temporal]
      (expr: E[F])
      (implicit ev: Expr[E, F])
    extends NodeDateTimeOps[E, F, DT](expr, DateTimeAdapter[DT], ev)
}
