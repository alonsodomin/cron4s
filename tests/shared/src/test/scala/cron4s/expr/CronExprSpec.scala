package cron4s.expr

import cron4s._
import org.scalatest.{FlatSpec, Matchers}
import shapeless._

/**
  * Created by alonsodomin on 07/08/2016.
  */
class CronExprSpec extends FlatSpec with Matchers {
  import CronField._

  val secondExpr     = ConstExpr(Second, 15)
  val minuteExpr     = ConstExpr(Minute, 10)
  val hourExpr       = ConstExpr(Hour, 4)
  val dayOfMonthExpr = ConstExpr(DayOfMonth, 12)
  val monthExpr      = ConstExpr(Month, 6)
  val dayOfWeekExpr  = ConstExpr(DayOfWeek, 3)

  val expr = CronExpr(secondExpr :: minuteExpr :: hourExpr :: dayOfMonthExpr ::
    monthExpr :: dayOfWeekExpr :: HNil)

  "field" should "return the expression for the correct cron field" in {
    expr.field(Second) shouldBe secondExpr
    expr.seconds shouldBe secondExpr

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

  "timePart" should "return the time relative part of the expression" in {
    expr.timePart shouldBe (secondExpr :: minuteExpr :: hourExpr :: HNil)
  }

  "datePart" should "return the date relative part of the expression" in {
    expr.datePart shouldBe (dayOfMonthExpr :: monthExpr :: dayOfWeekExpr :: HNil)
  }

}
