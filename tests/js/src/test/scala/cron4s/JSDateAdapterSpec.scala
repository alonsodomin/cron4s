package cron4s

import cron4s.testkit.DateTimeAdapterTestKit

import scala.scalajs.js.Date

import js._

/**
  * Created by alonsodomin on 02/09/2016.
  */
class JSDateAdapterSpec extends DateTimeAdapterTestKit[Date]("JSDate") with JSTestBase
