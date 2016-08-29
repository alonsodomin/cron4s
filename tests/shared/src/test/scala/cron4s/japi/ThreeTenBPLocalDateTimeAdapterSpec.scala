package cron4s.japi

import cron4s.testkit.DateTimeAdapterTestKit
import threetenbp._

import org.threeten.bp.LocalDateTime

import scalaz.Equal

/**
  * Created by alonsodomin on 29/08/2016.
  */
class ThreeTenBPLocalDateTimeAdapterSpec extends DateTimeAdapterTestKit[LocalDateTime]("ThreeTenBPLocalDateTime")
  with ThreeTenBPLocalDateTimeTestBase