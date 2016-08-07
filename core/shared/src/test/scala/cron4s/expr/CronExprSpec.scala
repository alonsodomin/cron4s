package cron4s.expr

import cron4s._

import shapeless._

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by alonsodomin on 07/08/2016.
  */
class CronExprSpec extends FlatSpec with Matchers {
  import CronField._

  val minuteExpr     = ConstExpr(Minute, 10)
  val hourExpr       = ConstExpr(Hour, 4)
  val dayOfMonthExpr = ConstExpr(DayOfMonth, 12)
  val monthExpr      = ConstExpr(Month, 6)
  val dayOfWeekExpr  = ConstExpr(DayOfWeek, 3)

  val expr = CronExpr(minuteExpr :: hourExpr :: dayOfMonthExpr ::
    monthExpr :: dayOfWeekExpr :: HNil)

  "CronExpr.field" should "return the expression for the correct cron field" in {
    expr.field(Minute) shouldBe minuteExpr
    expr.minutes shouldBe minuteExpr

    expr.field(Hour) shouldBe hourExpr
    expr.hours shouldBe hourExpr

    expr.field(DayOfMonth) shouldBe dayOfMonthExpr
    expr.daysOfMonth shouldBe dayOfMonthExpr

    expr.field(Month) shouldBe monthExpr
    expr.months shouldBe monthExpr

    expr.field(DayOfWeek) shouldBe dayOfWeekExpr
    expr.daysOfWeek shouldBe dayOfWeekExpr
  }

}
