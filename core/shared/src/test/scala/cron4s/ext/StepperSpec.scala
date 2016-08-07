package cron4s.ext

import cron4s._
import cron4s.expr._

import shapeless._

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

/**
  * Created by alonsodomin on 07/08/2016.
  */
class StepperSpec extends PropSpec with TableDrivenPropertyChecks with Matchers {
  import CronField._
  import testdummy._

  val onlyTuesdaysAt12 = CronExpr(ConstExpr(Minute, 0) :: ConstExpr(Hour, 12) ::
    AnyExpr[DayOfMonth.type] :: AnyExpr[Month.type] :: ConstExpr(DayOfWeek, 1) :: HNil)

  val sample = Table(
    ("expr", "from", "stepSize", "expected"),
    (onlyTuesdaysAt12, DummyDateTime(0, 0, 1, 8, 0), 1, DummyDateTime(0, 12, 1, 8, 1))
  )

  property("step") {
    forAll(sample) { (expr: CronExpr, from: DummyDateTime, stepSize: Int, expected: DummyDateTime) =>
      expr.step(from, stepSize) shouldBe Some(expected)
    }
  }

}
