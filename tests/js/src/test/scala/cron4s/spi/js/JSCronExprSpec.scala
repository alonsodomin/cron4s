package cron4s.spi.js

import cron4s.testkit.ExtendedCronExprTestKit
import org.scalatest.Ignore

import scala.scalajs.js.Date

/**
  * Created by alonsodomin on 29/08/2016.
  */
@Ignore
abstract class JSCronExprSpec extends ExtendedCronExprTestKit[Date] with JSTestBase
