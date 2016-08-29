package cron4s.japi

import cron4s.testkit.ExtensionsTestKitBase
import org.threeten.bp.{ZoneId, ZonedDateTime}

import scalaz.Equal

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait ThreeTenBPZonedDateTimeTestBase extends ExtensionsTestKitBase[ZonedDateTime] {
  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): ZonedDateTime =
    ZonedDateTime.of(2016, month, dayOfMonth, hours, minutes, seconds, 0, ZoneId.of("UTC"))

  implicit val dateTimeEq: Equal[ZonedDateTime] = Equal.equal((lhs, rhs) => lhs.equals(rhs))
}
