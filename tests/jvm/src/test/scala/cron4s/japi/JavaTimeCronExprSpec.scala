package cron4s.japi

import java.time.LocalDateTime

import cron4s.testkit.ExtendedCronExprTestKit

import time._

/**
  * Created by alonsodomin on 29/08/2016.
  */
class JavaTimeCronExprSpec extends ExtendedCronExprTestKit[LocalDateTime] with LocalDateTimeTestBase
