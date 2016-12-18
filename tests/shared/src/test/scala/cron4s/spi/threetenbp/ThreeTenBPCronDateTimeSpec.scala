package cron4s.spi.threetenbp

import cron4s.testkit.CronDateTimeTestKit
import org.threeten.bp.LocalDateTime

/**
  * Created by alonsodomin on 29/08/2016.
  */
class ThreeTenBPCronDateTimeSpec extends CronDateTimeTestKit[LocalDateTime] with ThreeTenBPLocalDateTimeTestBase
