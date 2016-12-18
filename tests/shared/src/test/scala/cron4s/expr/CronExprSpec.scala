package cron4s.expr

import cron4s._

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by alonsodomin on 07/08/2016.
  */
class CronExprSpec extends FlatSpec with Matchers {
  import CronField._

  val secondExpr     = ConstExpr[Second](15)
  val minuteExpr     = ConstExpr[Minute](10)
  val hourExpr       = ConstExpr[Hour](4)
  val dayOfMonthExpr = ConstExpr[DayOfMonth](12)
  val monthExpr      = ConstExpr[Month](6)
  val dayOfWeekExpr  = ConstExpr[DayOfWeek](3)

  val timePart = TimePartExpr(secondExpr, minuteExpr, hourExpr)
  val datePart = DatePartExpr(dayOfMonthExpr, monthExpr, dayOfWeekExpr)

  val expr = CronExpr(secondExpr, minuteExpr, hourExpr, dayOfMonthExpr, monthExpr, dayOfWeekExpr)

  "field" should "return the expression for the correct cron field" in {
    expr.field[Second] shouldBe expr.seconds
    expr.seconds.select[ConstExpr[Second]] shouldBe Some(secondExpr)

    expr.field[Minute] shouldBe expr.minutes
    expr.minutes.select[ConstExpr[Minute]] shouldBe Some(minuteExpr)

    expr.field[Hour] shouldBe expr.hours
    expr.hours.select[ConstExpr[Hour]] shouldBe Some(hourExpr)

    expr.field[DayOfMonth] shouldBe expr.daysOfMonth
    expr.daysOfMonth.select[ConstExpr[DayOfMonth]] shouldBe Some(dayOfMonthExpr)

    expr.field[Month] shouldBe expr.months
    expr.months.select[ConstExpr[Month]] shouldBe Some(monthExpr)

    expr.field[DayOfWeek] shouldBe expr.daysOfWeek
    expr.daysOfWeek.select[ConstExpr[DayOfWeek]] shouldBe Some(dayOfWeekExpr)

    expr.toString shouldBe "15 10 4 12 6 3"
  }

  "timePart" should "return the time relative part of the expression" in {
    expr.timePart shouldBe timePart

    timePart.seconds.select[ConstExpr[Second]] shouldBe Some(secondExpr)
    timePart.minutes.select[ConstExpr[Minute]] shouldBe Some(minuteExpr)
    timePart.hours.select[ConstExpr[Hour]] shouldBe Some(hourExpr)

    timePart.toString shouldBe "15 10 4"
  }

  "datePart" should "return the date relative part of the expression" in {
    expr.datePart shouldBe datePart

    datePart.daysOfMonth.select[ConstExpr[DayOfMonth]] shouldBe Some(dayOfMonthExpr)
    datePart.months.select[ConstExpr[Month]] shouldBe Some(monthExpr)
    datePart.daysOfWeek.select[ConstExpr[DayOfWeek]] shouldBe Some(dayOfWeekExpr)

    datePart.toString shouldBe "12 6 3"
  }

}
