package cron4s.japi

import java.util.Calendar

import cron4s.CronField
import cron4s.expr.{CronExpr, Expr}
import cron4s.ext.{DateTimeAdapter, ExtendedCronExpr, ExtendedExpr}
import cron4s.types.SequencedExpr

/**
  * Created by alonsodomin on 31/07/2016.
  */
object calendar {
  import CronField._

  implicit object JavaCalendarAdapter extends DateTimeAdapter[Calendar] {

    override def get[F <: CronField](dateTime: Calendar, field: F): Option[Int] = {
      val value = field match {
        case Minute     => dateTime.get(Calendar.MINUTE)
        case Hour       => dateTime.get(Calendar.HOUR_OF_DAY)
        case DayOfMonth => dateTime.get(Calendar.DAY_OF_MONTH)
        case Month      => dateTime.get(Calendar.MONTH) + 1
        case DayOfWeek  =>
          val dayOfWeek = dateTime.get(Calendar.DAY_OF_WEEK)
          (dayOfWeek - 2) % 7
      }
      Some(value)
    }

    override def set[F <: CronField](dateTime: Calendar, field: F, value: Int): Option[Calendar] = {
      def setter(set: Calendar => Unit): Calendar = {
        val newDateTime = dateTime.clone().asInstanceOf[Calendar]
        set(newDateTime)
        newDateTime
      }

      Some(field match {
        case Minute     => setter(_.set(Calendar.MINUTE, value))
        case Hour       => setter(_.set(Calendar.HOUR_OF_DAY, value))
        case DayOfMonth => setter(_.set(Calendar.DAY_OF_MONTH, value))
        case Month      => setter(_.set(Calendar.MONTH, value - 1))
        case DayOfWeek  => setter(_.set(Calendar.DAY_OF_WEEK, (value + 2) % 7))
      })
    }
  }

  implicit class CalendarCronExpr(expr: CronExpr) extends ExtendedCronExpr[Calendar](expr)
  implicit class CalendarExpr[E[_] <: Expr[_], F <: CronField]
      (expr: E[F])
      (implicit ev: SequencedExpr[E, F])
    extends ExtendedExpr[E, F, Calendar](expr)

}
