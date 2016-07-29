package cron4s

import cron4s.expr._

import org.widok.moment.Date

/**
  * Created by domingueza on 29/07/2016.
  */
object momentjs {
  import CronField._

  implicit def fieldExtractor(field: CronField, date: Date): Option[Int] = field match {
    case Minute     => Some(date.minute())
    case Hour       => Some(date.hour())
    case DayOfMonth => Some(date.day())
    case Month      => Some(date.month())
    case DayOfWeek  => Some(date.isoWeekday())
  }

  implicit class RichCronExpr(expr: CronExpr) extends RichCronExprBase[Date](expr)

}
