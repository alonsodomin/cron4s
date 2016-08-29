package cron4s.japi

import java.time.{LocalDateTime, ZoneOffset}
import java.util.{Calendar, TimeZone}

import cron4s.CronField
import cron4s.japi.calendar.JavaCalendarAdapter
import cron4s.testkit.ExtensionsTestKitBase

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait CalendarTestBase extends ExtensionsTestKitBase[Calendar] {

  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): Calendar = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    val timeMillis = LocalDateTime.of(2016, month, dayOfMonth, hours, minutes, seconds).
      toInstant(ZoneOffset.UTC).toEpochMilli
    cal.setTimeInMillis(timeMillis)

    JavaCalendarAdapter.set(cal, CronField.DayOfWeek, dayOfWeek).get
  }

}
