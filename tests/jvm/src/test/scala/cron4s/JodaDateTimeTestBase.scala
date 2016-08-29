package cron4s

import cron4s.testkit.ExtensionsTestKitBase

import org.joda.time.DateTime

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait JodaDateTimeTestBase extends ExtensionsTestKitBase[DateTime] {

  protected def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): DateTime =
    new DateTime(2016, month, dayOfMonth, hours, minutes, seconds)

}
