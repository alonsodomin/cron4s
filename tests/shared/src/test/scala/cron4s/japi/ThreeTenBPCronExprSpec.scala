package cron4s.japi

import cron4s.testkit.ExtendedCronExprTestKit
import org.threeten.bp.LocalDateTime

import threetenbp._

/**
  * Created by alonsodomin on 29/08/2016.
  */
class ThreeTenBPCronExprSpec extends ExtendedCronExprTestKit[LocalDateTime] with ThreeTenBPLocalDateTimeTestBase
