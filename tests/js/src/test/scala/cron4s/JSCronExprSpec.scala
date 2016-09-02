package cron4s

import cron4s.testkit.ExtendedCronExprTestKit

import scala.scalajs.js.Date
import js._
import org.scalatest.Ignore

/**
  * Created by alonsodomin on 29/08/2016.
  */
@Ignore
abstract class JSCronExprSpec extends ExtendedCronExprTestKit[Date] with JSTestBase
