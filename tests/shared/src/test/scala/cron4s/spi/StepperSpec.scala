package cron4s.spi

import cron4s.CronField
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

  val onlyTuesdaysAt12 = CronExpr(
    ConstNode[Second](0),
    ConstNode[Minute](0),
    ConstNode[Hour](12),
    EachNode[DayOfMonth],
    EachNode[Month],
    ConstNode[DayOfWeek](1)
  )
  val everyMinuteBetween2And3 = CronExpr(
    ConstNode[Second](0),
    EachNode[Minute],
    BetweenNode(ConstNode[Hour](2), ConstNode[Hour](3)),
    EachNode[DayOfMonth],
    EachNode[Month],
    EachNode[DayOfWeek]
  )

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
