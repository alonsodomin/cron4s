package cron4s.spi.threetenbp

import cron4s.testkit.ExtendedCronExprTestKit
import org.threeten.bp.LocalDateTime

/**
  * Created by alonsodomin on 29/08/2016.
  */
class ThreeTenBPCronExprSpec extends ExtendedCronExprTestKit[LocalDateTime] with ThreeTenBPLocalDateTimeTestBase
