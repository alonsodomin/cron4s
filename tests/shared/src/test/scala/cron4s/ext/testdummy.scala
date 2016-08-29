package cron4s.ext

import cron4s._
import cron4s.expr.{CronExpr, Expr}
import cron4s.types.IsFieldExpr

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

  implicit class DummyCronExpr(expr: CronExpr) extends ExtendedCronExpr[DummyDateTime](expr)
  implicit class DummyExpr[E[_ <: CronField] <: Expr[F], F <: CronField]
      (expr: E[F])
      (implicit ev: IsFieldExpr[E, F])
    extends ExtendedExpr[E, F, DummyDateTime](expr)

}
