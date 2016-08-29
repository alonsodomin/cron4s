package cron4s.japi

import java.time.LocalDateTime

import cron4s.testkit.DateTimeAdapterTestKit
import time._

import scalaz.Equal

/**
  * Created by alonsodomin on 29/08/2016.
  */
class LocalDateTimeAdapterSpec extends DateTimeAdapterTestKit[LocalDateTime]("LocalDateTime") with LocalDateTimeTestBase
