package cron4s.datetime.js

import cron4s.testkit.DateTimeTestKitBase

import scala.scalajs.js.Date

/**
  * Created by alonsodomin on 02/09/2016.
  */
trait JSTestBase extends DateTimeTestKitBase[Date] {

  protected def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): Date =
    new Date(2016, month - 1, dayOfMonth, hours, minutes, seconds)

}
