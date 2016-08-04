package cron4s.ext

import cron4s._
import cron4s.expr.{CronExpr, Expr}
import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by alonsodomin on 04/08/2016.
  */
object testdummy {
  import CronField._
  import CronUnit._

  implicit lazy val arbitraryDummyDateTime = Arbitrary(for {
    minutes     <- Gen.choose(Minutes.min, Minutes.max)
    hours       <- Gen.choose(Hours.min, Hours.max)
    daysOfMonth <- Gen.choose(DaysOfMonth.min, DaysOfMonth.max)
    months      <- Gen.choose(Months.min, Months.max)
    daysOfWeek  <- Gen.choose(DaysOfWeek.min, DaysOfWeek.max)
  } yield DummyDateTime(minutes, hours, daysOfMonth, months, daysOfWeek))

  implicit object TestDummyAdapter extends DateTimeAdapter[DummyDateTime] {
    override def get[F <: CronField](dateTime: DummyDateTime, field: F): Option[Int] = Some(field match {
      case Minute     => dateTime.minutes
      case Hour       => dateTime.hours
      case DayOfMonth => dateTime.dayOfMonth
      case Month      => dateTime.month
      case DayOfWeek  => dateTime.dayOfWeek
    })

    override def set[F <: CronField](dateTime: DummyDateTime, field: F, value: Int): Option[DummyDateTime] = {
      Some(field match {
        case Minute     => dateTime.copy(minutes = value)
        case Hour       => dateTime.copy(hours = value)
        case DayOfMonth => dateTime.copy(dayOfMonth = value)
        case Month      => dateTime.copy(month = value)
        case DayOfWeek  => dateTime.copy(dayOfWeek = value)
      })
    }
  }

  implicit class DummyCronExpr(expr: CronExpr) extends ExtendedCronExpr[DummyDateTime](expr)
  implicit class DummyExpr[F <: CronField](expr: Expr[F]) extends ExtendedExpr[F, DummyDateTime](expr)

}
