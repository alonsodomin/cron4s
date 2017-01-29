package cron4s.datetime

import cron4s._
import cron4s.expr.CronExpr
import cron4s.types.Expr

import scalaz.Equal

/**
  * Created by alonsodomin on 04/08/2016.
  */
object testdummy {
  import CronField._

  implicit val dummyDTInstance = Equal.equalA[DummyDateTime]

  implicit object TestDummyAdapter extends DateTimeAdapter[DummyDateTime] {
    override def get[F <: CronField](dateTime: DummyDateTime, field: F): Option[Int] = Some(field match {
      case Second     => dateTime.seconds
      case Minute     => dateTime.minutes
      case Hour       => dateTime.hours
      case DayOfMonth => dateTime.dayOfMonth
      case Month      => dateTime.month
      case DayOfWeek  => dateTime.dayOfWeek
    })

    override def set[F <: CronField](dateTime: DummyDateTime, field: F, value: Int): Option[DummyDateTime] = {
      Some(field match {
        case Second     => dateTime.copy(seconds = value)
        case Minute     => dateTime.copy(minutes = value)
        case Hour       => dateTime.copy(hours = value)
        case DayOfMonth => dateTime.copy(dayOfMonth = value)
        case Month      => dateTime.copy(month = value)
        case DayOfWeek  => dateTime.copy(dayOfWeek = value)
      })
    }
  }

  implicit class DummyCronExpr(expr: CronExpr) extends CronDateTimeOps[DummyDateTime](expr)
  implicit class DummyNode[E[_ <: CronField], F <: CronField]
      (expr: E[F])
      (implicit ev: Expr[E, F])
    extends NodeDateTimeOps[E, F, DummyDateTime](expr, TestDummyAdapter, ev)

}
