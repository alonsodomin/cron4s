package cron4s.japi

import java.time.{LocalDateTime, ZoneOffset}
import java.util.{Calendar, TimeZone}

import cron4s.CronField
import cron4s.testkit.ExtendedCronExprTestKit

import calendar._

/**
  * Created by alonsodomin on 29/08/2016.
  */
class CalendarCronExprSpec extends ExtendedCronExprTestKit[Calendar] {

  def createDateTime(minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): Calendar = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    val timeMillis = LocalDateTime.of(2016, month, dayOfMonth, hours, minutes).
      toInstant(ZoneOffset.UTC).toEpochMilli
    cal.setTimeInMillis(timeMillis)

    JavaCalendarAdapter.set(cal, CronField.DayOfWeek, dayOfWeek).get
  }

}
