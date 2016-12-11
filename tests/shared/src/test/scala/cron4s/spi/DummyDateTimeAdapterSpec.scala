package cron4s.ext

import cron4s.testkit.DateTimeAdapterTestKit
import testdummy._

/**
  * Created by alonsodomin on 29/08/2016.
  */
class DummyDateTimeAdapterSpec extends DateTimeAdapterTestKit[DummyDateTime]("DummyDateTime") with DummyTestBase

