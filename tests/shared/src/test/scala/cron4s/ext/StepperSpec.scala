package cron4s.ext

import cron4s._
import cron4s.expr._
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}
import shapeless._

/**
  * Created by alonsodomin on 07/08/2016.
  */
class StepperSpec extends PropSpec with TableDrivenPropertyChecks with Matchers {
  import CronField._
  import testdummy._

  val onlyTuesdaysAt12 = CronExpr(ConstExpr(Second, 0) :: ConstExpr(Minute, 0) :: ConstExpr(Hour, 12) ::
    AnyExpr[DayOfMonth.type] :: AnyExpr[Month.type] :: ConstExpr(DayOfWeek, 1) :: HNil)
  val everyMinuteBetween2And3 = CronExpr(ConstExpr(Second, 0) :: AnyExpr[Minute.type] :: BetweenExpr(ConstExpr(Hour, 2), ConstExpr(Hour, 3)) ::
    AnyExpr[DayOfMonth.type] :: AnyExpr[Month.type] :: AnyExpr[DayOfWeek.type] :: HNil)

  val sample = Table(
    ("expr", "from", "stepSize", "expected"),
    (onlyTuesdaysAt12, DummyDateTime(0, 0, 0, 1, 8, 0), 1, DummyDateTime(0, 0, 12, 2, 8, 1)),
    (everyMinuteBetween2And3, DummyDateTime(0, 0, 2, 1, 1, 1), 1, DummyDateTime(0, 1, 2, 1, 1, 1)),
    (everyMinuteBetween2And3, DummyDateTime(0, 59, 2, 1, 1, 1), 1, DummyDateTime(0, 0, 3, 1, 1, 1))
  )

  property("step") {
    forAll(sample) { (expr: CronExpr, from: DummyDateTime, stepSize: Int, expected: DummyDateTime) =>
      expr.step(from, stepSize) shouldBe Some(expected)
    }
  }

}
