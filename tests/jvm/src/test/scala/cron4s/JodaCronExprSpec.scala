package cron4s

import cron4s.testkit.ExtendedCronExprTestKit

import org.joda.time.DateTime

import joda._

/**
  * Created by alonsodomin on 29/08/2016.
  */
class JodaCronExprSpec extends ExtendedCronExprTestKit[DateTime] {

  protected def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): DateTime =
    new DateTime(2016, month, dayOfMonth, hours, minutes, seconds)

}
