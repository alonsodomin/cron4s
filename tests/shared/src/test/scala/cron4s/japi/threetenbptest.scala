package cron4s.japi

import cron4s.CronUnit
import org.scalacheck.{Arbitrary, Gen}
import org.threeten.bp.{LocalDateTime, ZoneId, ZonedDateTime}

import scalaz.Equal

/**
  * Created by alonsodomin on 29/08/2016.
  */
object threetenbptest {
  import CronUnit._

  implicit val arbitraryLocalDateTime = Arbitrary(for {
    seconds     <- Gen.choose(Seconds.min, Seconds.max)
    minutes     <- Gen.choose(Minutes.min, Minutes.max)
    hours       <- Gen.choose(Hours.min, Hours.max)
    daysOfMonth <- Gen.choose(DaysOfMonth.min, DaysOfMonth.max)
    months      <- Gen.const(1)
    daysOfWeek  <- Gen.choose(DaysOfWeek.min, DaysOfWeek.max)
  } yield LocalDateTime.of(2016, months, daysOfMonth, hours, minutes, seconds))

  implicit val localDateTimeEq = Equal.equal[LocalDateTime]((lhs, rhs) => lhs.equals(rhs))

  implicit val arbitraryZonedDateTime = Arbitrary(for {
    seconds     <- Gen.choose(Seconds.min, Seconds.max)
    minutes     <- Gen.choose(Minutes.min, Minutes.max)
    hours       <- Gen.choose(Hours.min, Hours.max)
    daysOfMonth <- Gen.choose(DaysOfMonth.min, DaysOfMonth.max)
    months      <- Gen.const(1)
    daysOfWeek  <- Gen.choose(DaysOfWeek.min, DaysOfWeek.max)
  } yield ZonedDateTime.of(2016, months, daysOfMonth, hours, minutes, seconds, 0, ZoneId.of("UTC")))

  implicit val zonedDateTimeEq = Equal.equal[ZonedDateTime]((lhs, rhs) => lhs.equals(rhs))

}
