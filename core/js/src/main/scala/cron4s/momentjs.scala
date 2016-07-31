package cron4s

import cron4s.expr._
import cron4s.ext._

import org.widok.moment.Date

/**
  * Created by domingueza on 29/07/2016.
  */
object momentjs {
  import CronField._

  implicit object Adapter extends DateTimeAdapter[Date] {

    override def get[F <: CronField](dateTime: Date, field: F): Option[Int] = field match {
      case Minute => Some(dateTime.minute())
      case Hour => Some(dateTime.hour())
      case DayOfMonth => Some(dateTime.day())
      case Month => Some(dateTime.month())
      case DayOfWeek => Some(dateTime.isoWeekday())
    }

    override def set[F <: CronField](dateTime: Date, field: F, value: Int): Option[Date] = ???
  }

  implicit class MomentJSCronExpr(expr: CronExpr) extends ExtendedCronExpr[Date](expr)
  implicit class MomentJSExpr[F <: CronField](expr: Expr[F]) extends ExtendedExpr[F, Date](expr)

}
