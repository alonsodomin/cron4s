package cron4s.spi.threetenbp

import cron4s.testkit.DateTimeAdapterTestKit

import org.threeten.bp.ZonedDateTime

/**
  * Created by alonsodomin on 29/08/2016.
  */
class ThreeTenBPZonedDateTimeAdapterSpec extends DateTimeAdapterTestKit[ZonedDateTime]("ThreeTenBPZonedDateTime")
  with ThreeTenBPZonedDateTimeTestBase