package cron4s.datetime.js

import cron4s.testkit.DateTimeAdapterTestKit

import scala.scalajs.js.Date

/**
  * Created by alonsodomin on 02/09/2016.
  */
class JSDateAdapterSpec extends DateTimeAdapterTestKit[Date]("JSDate") with JSTestBase
