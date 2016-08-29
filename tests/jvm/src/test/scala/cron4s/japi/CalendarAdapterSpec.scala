package cron4s.japi

import java.util.Calendar

import cron4s.testkit.DateTimeAdapterTestKit
import calendar._
import org.scalatest.Ignore

/**
  * Created by alonsodomin on 29/08/2016.
  */
@Ignore
class CalendarAdapterSpec extends DateTimeAdapterTestKit[Calendar]("Calendar") with CalendarTestBase
