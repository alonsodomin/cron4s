package cron4s

import cron4s.expr._

import shapeless._

import scala.scalajs.js.Date

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

/**
  * Created by alonsodomin on 08/08/2016.
  */
class JsSpec extends PropSpec with TableDrivenPropertyChecks with Matchers {
  import CronField._
  import js._

  val onlyTuesdaysAt12 = CronExpr(ConstExpr(Minute, 0) :: ConstExpr(Hour, 12) ::
    AnyExpr[DayOfMonth.type] :: AnyExpr[Month.type] :: ConstExpr(DayOfWeek, 1) :: HNil)

  val samples = Table(
    ("expr", "from", "stepSize", "expected"),
    (onlyTuesdaysAt12, new Date(2016, 7, 1, 0, 0), 1, new Date(2016, 7, 2, 12, 0))
  )

  property("step") {
    forAll(samples) { (expr: CronExpr, from: Date, stepSize: Int, expected: Date) =>
      val adjusted = expr.step(from, stepSize)
      adjusted shouldBe defined
      //adjusted.get shouldBe expected
    }
  }

}
