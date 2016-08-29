package cron4s

import cron4s.expr.{CronExpr, Expr}
import cron4s.ext.{DateTimeAdapter, ExtendedCronExpr, ExtendedExpr}
import cron4s.types.IsFieldExpr

import scala.scalajs.js.Date

/**
  * Created by alonsodomin on 31/07/2016.
  */
object js {
  import CronField._

  implicit object JsAdapter extends DateTimeAdapter[Date] {
    override def get[F <: CronField](dateTime: Date, field: F): Option[Int] = {
      val value = field match {
        case Minute     => dateTime.getMinutes()
        case Hour       => dateTime.getHours()
        case DayOfMonth => dateTime.getDate()
        case Month      => dateTime.getMonth() + 1
        case DayOfWeek  =>
          val dayOfWeek = dateTime.getDay()
          (dayOfWeek - 1) % 7
      }

      Some(value)
    }

    override def set[F <: CronField](dateTime: Date, field: F, value: Int): Option[Date] = {
      def setter(setter: Date => Unit): Date = {
        val newDateTime = new Date(dateTime.getTime())
        setter(newDateTime)
        newDateTime
      }

      Some(field match {
        case Minute     => setter(_.setMinutes(value))
        case Hour       => setter(_.setHours(value))
        case DayOfMonth => setter(_.setDate(value))
        case Month      => setter(_.setMonth(value - 1))
        case DayOfWeek  =>
          val dayToSet = (value + 1) % 7
          val offset = dayToSet - dateTime.getDay()
          setter(d => d.setDate(d.getDate() + offset))
      })
    }
  }

  implicit class JSCronExpr(expr: CronExpr) extends ExtendedCronExpr[Date](expr)
  implicit class JSExpr[E[_ <: CronField] <: Expr[_], F <: CronField]
      (expr: E[F])
      (implicit ev: IsFieldExpr[E, F])
    extends ExtendedExpr[E, F, Date](expr)

}
