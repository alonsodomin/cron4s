package cron4s.testkit

import cron4s.CronField._
import cron4s.expr.{AnyExpr, ConstExpr, CronExpr}
import cron4s.ext.{DateTimeAdapter, ExtendedCronExpr}

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

import shapeless._

/**
  * Created by alonsodomin on 29/08/2016.
  */
abstract class ExtendedCronExprTestKit[DateTime : DateTimeAdapter]
  extends PropSpec with TableDrivenPropertyChecks with Matchers {

  val onlyTuesdaysAt12 = CronExpr(ConstExpr(Minute, 0) :: ConstExpr(Hour, 12) ::
    AnyExpr[DayOfMonth.type] :: AnyExpr[Month.type] :: ConstExpr(DayOfWeek, 1) :: HNil)
  val onlySundays = CronExpr(AnyExpr[Minute.type] :: AnyExpr[Hour.type] ::
    AnyExpr[DayOfMonth.type] :: AnyExpr[Month.type] :: ConstExpr(DayOfWeek, 6) :: HNil)

  lazy val samples = Table(
    ("expr", "from", "stepSize", "expected"),
    (onlyTuesdaysAt12, createDateTime(0, 0, 1, 8, 0), 1, createDateTime(0, 12, 2, 8, 1)),
    (onlySundays, createDateTime(0, 0, 1, 8, 0), 1, createDateTime(1, 0, 7, 8, 6))
  )

  protected def createDateTime(minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): DateTime

  property("step") {
    forAll(samples) { (expr: CronExpr, initial: DateTime, stepSize: Int, expected: DateTime) =>
      val extCronExpr = new ExtendedCronExpr[DateTime](expr) { }
      extCronExpr.step(initial, stepSize) shouldBe Some(expected)
    }
  }

}
