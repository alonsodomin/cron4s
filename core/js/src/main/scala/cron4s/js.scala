package cron4s

import cron4s.expr.{CronExpr, Expr}
import cron4s.ext.{DateTimeAdapter, ExtendedCronExpr, ExtendedExpr}

import scala.scalajs.js.Date

/**
  * Created by alonsodomin on 31/07/2016.
  */
object js {
  import CronField._

  implicit object JsAdapter extends DateTimeAdapter[Date] {
    override def get[F <: CronField](dateTime: Date, field: F): Option[Int] = field match {
      case Minute     => Some(dateTime.getMinutes())
      case Hour       => Some(dateTime.getHours())
      case DayOfMonth => Some(dateTime.getDate())
      case Month      => Some(dateTime.getMonth() + 1)
      case DayOfWeek  => Some(dateTime.getDay())
    }

    override def set[F <: CronField](dateTime: Date, field: F, value: Int): Option[Date] = {
      def setter(setter: Date => Unit): Date = {
        setter(dateTime)
        dateTime
      }

      field match {
        case Minute     => Some(setter(_.setMinutes(value)))
        case Hour       => Some(setter(_.setHours(value)))
        case DayOfMonth => Some(setter(_.setDate(value)))
        case Month      => Some(setter(_.setMonth(value)))
        case DayOfWeek  => Some(dateTime)
      }
    }
  }

  implicit class JSCronExpr(expr: CronExpr) extends ExtendedCronExpr[Date](expr)
  implicit class JSExpr[F <: CronField](expr: Expr[F]) extends ExtendedExpr[F, Date](expr)

}
