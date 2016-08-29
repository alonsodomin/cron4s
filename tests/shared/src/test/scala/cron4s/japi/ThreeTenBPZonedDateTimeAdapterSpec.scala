package cron4s.japi

import cron4s.testkit.DateTimeAdapterTestKit
import threetenbp._

import org.threeten.bp.ZonedDateTime

/**
  * Created by alonsodomin on 29/08/2016.
  */
class ThreeTenBPZonedDateTimeAdapterSpec extends DateTimeAdapterTestKit[ZonedDateTime]("ThreeTenBPZonedDateTime")
  with ThreeTenBPZonedDateTimeTestBase