package cron4s.spi.javatime

import java.time.LocalDateTime

import cron4s.testkit.DateTimeAdapterTestKit

/**
  * Created by alonsodomin on 29/08/2016.
  */
class LocalDateTimeAdapterSpec extends DateTimeAdapterTestKit[LocalDateTime]("LocalDateTime") with LocalDateTimeTestBase
