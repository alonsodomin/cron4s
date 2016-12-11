package cron4s.spi.joda

import cron4s.testkit.ExtendedCronExprTestKit
import org.joda.time.DateTime

/**
  * Created by alonsodomin on 29/08/2016.
  */
class JodaCronExprSpec extends ExtendedCronExprTestKit[DateTime] with JodaTestBase
