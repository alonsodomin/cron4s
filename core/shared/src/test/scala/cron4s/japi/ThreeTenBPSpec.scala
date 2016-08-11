package cron4s.japi

import cron4s.CronField._
import cron4s.expr.{AnyExpr, ConstExpr, CronExpr}

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

import shapeless._

import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.Temporal

/**
  * Created by alonsodomin on 11/08/2016.
  */
class ThreeTenBPSpec extends PropSpec with TableDrivenPropertyChecks with Matchers {
  import threetenbp._

  val onlyTuesdaysAt12 = CronExpr(ConstExpr(Minute, 0) :: ConstExpr(Hour, 12) ::
    AnyExpr[DayOfMonth.type] :: AnyExpr[Month.type] :: ConstExpr(DayOfWeek, 1) :: HNil)
  val onlySundays = CronExpr(AnyExpr[Minute.type] :: AnyExpr[Hour.type] ::
    AnyExpr[DayOfMonth.type] :: AnyExpr[Month.type] :: ConstExpr(DayOfWeek, 6) :: HNil)

  val samples = Table(
    ("expr", "from", "stepSize", "expected"),
    (onlyTuesdaysAt12, LocalDateTime.of(2016, 8, 1, 0, 0), 1, LocalDateTime.of(2016, 8, 2, 12, 0)),
    (onlySundays, LocalDateTime.of(2016, 8, 1, 0, 0), 1, LocalDateTime.of(2016, 8, 7, 0, 1))
  )

  property("step") {
    forAll(samples) { (expr: CronExpr, initial: Temporal, stepSize: Int, expected: Temporal) =>
      expr.step(initial, stepSize) shouldBe Some(expected)
    }
  }

}
