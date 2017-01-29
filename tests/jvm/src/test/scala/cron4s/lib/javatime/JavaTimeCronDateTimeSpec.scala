package cron4s.lib.javatime

import java.time.LocalDateTime

import cron4s.testkit.CronDateTimeTestKit

/**
  * Created by alonsodomin on 29/08/2016.
  */
class JavaTimeCronDateTimeSpec extends CronDateTimeTestKit[LocalDateTime] with LocalDateTimeTestBase
