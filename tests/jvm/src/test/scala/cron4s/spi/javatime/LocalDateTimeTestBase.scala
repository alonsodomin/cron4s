package cron4s.spi.javatime

import java.time.LocalDateTime

import cron4s.testkit.ExtensionsTestKitBase

import scalaz.Equal

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait LocalDateTimeTestBase extends ExtensionsTestKitBase[LocalDateTime] {
  implicit val dateTimeEq: Equal[LocalDateTime] = Equal.equal((lhs, rhs) => lhs.equals(rhs))

  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): LocalDateTime =
    LocalDateTime.of(2016, month, dayOfMonth, hours, minutes, seconds)
}
