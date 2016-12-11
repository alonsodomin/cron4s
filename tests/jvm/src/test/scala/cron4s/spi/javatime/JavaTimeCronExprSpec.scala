package cron4s.spi.javatime

import java.time.LocalDateTime

import cron4s.testkit.ExtendedCronExprTestKit

/**
  * Created by alonsodomin on 29/08/2016.
  */
class JavaTimeCronExprSpec extends ExtendedCronExprTestKit[LocalDateTime] with LocalDateTimeTestBase
