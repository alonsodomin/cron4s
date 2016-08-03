package cron4s.japi

import java.util.Calendar

import cron4s.CronField
import cron4s.expr.{CronExpr, Expr}
import cron4s.ext.{DateTimeAdapter, ExtendedCronExpr, ExtendedExpr}

/**
  * Created by alonsodomin on 31/07/2016.
  */
object calendar {
  import CronField._

  implicit object Adapter extends DateTimeAdapter[Calendar] {

    override def get[F <: CronField](dateTime: Calendar, field: F): Option[Int] = Some(field match {
      case Minute     => dateTime.get(Calendar.MINUTE)
      case Hour       => dateTime.get(Calendar.HOUR_OF_DAY)
      case DayOfMonth => dateTime.get(Calendar.DAY_OF_MONTH)
      case Month      => dateTime.get(Calendar.MONTH)
      case DayOfWeek  => dateTime.get(Calendar.DAY_OF_WEEK)
    })

    override def set[F <: CronField](dateTime: Calendar, field: F, value: Int): Option[Calendar] = {
      def setter(set: Calendar => Unit): Calendar = {
        set(dateTime)
        dateTime
      }

      Some(field match {
        case Minute     => setter(_.set(Calendar.MINUTE, value))
        case Hour       => setter(_.set(Calendar.HOUR_OF_DAY, value))
        case DayOfMonth => setter(_.set(Calendar.DAY_OF_MONTH, value))
        case Month      => setter(_.set(Calendar.MONTH, value))
        case DayOfWeek  => setter(_.set(Calendar.DAY_OF_WEEK, value))
      })
    }
  }

  implicit class CalendarCronExpr(expr: CronExpr) extends ExtendedCronExpr[Calendar](expr)
  implicit class CalendarExpr[F <: CronField](expr: Expr[F]) extends ExtendedExpr[F, Calendar](expr)

}
