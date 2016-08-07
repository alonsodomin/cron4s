package cron4s.japi

import java.time._
import java.time.temporal.Temporal

import cron4s._
import cron4s.expr._

import shapeless._

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

/**
  * Created by alonsodomin on 07/08/2016.
  */
class JavaTimeSpec extends PropSpec with TableDrivenPropertyChecks with Matchers {
  import CronField._
  import time._

  val onlyTuesdaysAt12 = CronExpr(ConstExpr(Minute, 0) :: ConstExpr(Hour, 12) ::
    AnyExpr[DayOfMonth.type] :: AnyExpr[Month.type] :: ConstExpr(DayOfWeek, 1) :: HNil)

  val samples = Table(
    ("expr", "from", "stepSize", "expected"),
    (onlyTuesdaysAt12, LocalDateTime.of(2016, 8, 1, 0, 0), 1, LocalDateTime.of(2016, 8, 2, 12, 0))
  )

  property("step") {
    forAll(samples) { (expr: CronExpr, initial: Temporal, stepSize: Int, expected: Temporal) =>
      expr.step(initial, stepSize) shouldBe Some(expected)
    }
  }

}
