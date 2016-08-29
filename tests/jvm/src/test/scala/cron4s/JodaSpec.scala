package cron4s

import cron4s.expr._

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}

import shapeless._

import org.joda.time.DateTime

/**
  * Created by alonsodomin on 08/08/2016.
  */
class JodaSpec extends PropSpec with TableDrivenPropertyChecks with Matchers {
  import CronField._
  import joda._

  val onlyTuesdaysAt12 = CronExpr(ConstExpr(Minute, 0) :: ConstExpr(Hour, 12) ::
    AnyExpr[DayOfMonth.type] :: AnyExpr[Month.type] :: ConstExpr(DayOfWeek, 1) :: HNil)

  val samples = Table(
    ("expr", "from", "stepSize", "expected"),
    (onlyTuesdaysAt12, new DateTime(2016, 8, 1, 0, 0), 1, new DateTime(2016, 8, 2, 12, 0))
  )

  property("step") {
    forAll(samples) { (expr: CronExpr, from: DateTime, stepSize: Int, expected: DateTime) =>
      expr.step(from, stepSize) shouldBe Some(expected)
    }
  }

}
