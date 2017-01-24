package cron4s.expr

import cron4s._

import org.scalatest.{FlatSpec, Matchers}

import scalaz.syntax.show._

/**
  * Created by alonsodomin on 07/08/2016.
  */
class CronExprSpec extends FlatSpec with Matchers {
  import CronField._

  val secondExpr     = ConstNode[Second](15)
  val minuteExpr     = ConstNode[Minute](10)
  val hourExpr       = ConstNode[Hour](4)
  val dayOfMonthExpr = ConstNode[DayOfMonth](12)
  val monthExpr      = ConstNode[Month](6)
  val dayOfWeekExpr  = ConstNode[DayOfWeek](3)

  val timePart = TimeCronExpr(secondExpr, minuteExpr, hourExpr)
  val datePart = DateCronExpr(dayOfMonthExpr, monthExpr, dayOfWeekExpr)

  val expr = CronExpr(secondExpr, minuteExpr, hourExpr, dayOfMonthExpr, monthExpr, dayOfWeekExpr)

  "field" should "return the expression for the correct cron field" in {
    expr.field[Second] shouldBe expr.seconds
    expr.seconds.select[ConstNode[Second]] shouldBe Some(secondExpr)

    expr.field[Minute] shouldBe expr.minutes
    expr.minutes.select[ConstNode[Minute]] shouldBe Some(minuteExpr)

    expr.field[Hour] shouldBe expr.hours
    expr.hours.select[ConstNode[Hour]] shouldBe Some(hourExpr)

    expr.field[DayOfMonth] shouldBe expr.daysOfMonth
    expr.daysOfMonth.select[ConstNode[DayOfMonth]] shouldBe Some(dayOfMonthExpr)

    expr.field[Month] shouldBe expr.months
    expr.months.select[ConstNode[Month]] shouldBe Some(monthExpr)

    expr.field[DayOfWeek] shouldBe expr.daysOfWeek
    expr.daysOfWeek.select[ConstNode[DayOfWeek]] shouldBe Some(dayOfWeekExpr)

    expr.shows shouldBe "15 10 4 12 6 3"
  }

  "timePart" should "return the time relative part of the expression" in {
    expr.timePart shouldBe timePart

    timePart.seconds.select[ConstNode[Second]] shouldBe Some(secondExpr)
    timePart.minutes.select[ConstNode[Minute]] shouldBe Some(minuteExpr)
    timePart.hours.select[ConstNode[Hour]] shouldBe Some(hourExpr)

    timePart.toString shouldBe "15 10 4"
  }

  "datePart" should "return the date relative part of the expression" in {
    expr.datePart shouldBe datePart

    datePart.daysOfMonth.select[ConstNode[DayOfMonth]] shouldBe Some(dayOfMonthExpr)
    datePart.months.select[ConstNode[Month]] shouldBe Some(monthExpr)
    datePart.daysOfWeek.select[ConstNode[DayOfWeek]] shouldBe Some(dayOfWeekExpr)

    datePart.toString shouldBe "12 6 3"
  }

}
