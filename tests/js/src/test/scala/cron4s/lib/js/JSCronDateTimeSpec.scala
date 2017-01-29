package cron4s.lib.js

import cron4s.testkit.CronDateTimeTestKit
import org.scalatest.Ignore

import scala.scalajs.js.Date

/**
  * Created by alonsodomin on 29/08/2016.
  */
@Ignore
abstract class JSCronDateTimeSpec extends CronDateTimeTestKit[Date] with JSTestBase
