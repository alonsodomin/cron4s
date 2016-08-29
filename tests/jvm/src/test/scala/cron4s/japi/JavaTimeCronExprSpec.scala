package cron4s.japi

import java.time.LocalDateTime

import cron4s.testkit.ExtendedCronExprTestKit

import time._

/**
  * Created by alonsodomin on 29/08/2016.
  */
class JavaTimeCronExprSpec extends ExtendedCronExprTestKit[LocalDateTime] {

  protected def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): LocalDateTime =
    LocalDateTime.of(2016, month, dayOfMonth, hours, minutes, seconds)

}
