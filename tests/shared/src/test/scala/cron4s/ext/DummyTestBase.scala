package cron4s.ext

import cron4s.testkit.ExtensionsTestKitBase

import scalaz.Equal

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait DummyTestBase extends ExtensionsTestKitBase[DummyDateTime] {
  implicit val dateTimeEq = Equal.equalA[DummyDateTime]

  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): DummyDateTime =
    DummyDateTime(seconds, minutes, hours, dayOfMonth, month, dayOfWeek)
}
