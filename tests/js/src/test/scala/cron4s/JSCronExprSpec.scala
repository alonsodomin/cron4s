package cron4s

import cron4s.testkit.{ExtendedCronExprTestKit, ExtensionsTestKitBase}

import scala.scalajs.js.Date
import js._
import org.scalatest.Ignore

/**
  * Created by alonsodomin on 29/08/2016.
  */
@Ignore
class JSCronExprSpec extends ExtendedCronExprTestKit[Date] with ExtensionsTestKitBase[Date] {

  protected def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): Date =
    new Date(2016, month, dayOfMonth, hours, minutes, seconds)

}
