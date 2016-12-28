package cron4s.testkit

import cron4s.CronField._
import cron4s.expr._
import cron4s.spi.{DateTimeAdapter, CronDateTimeOps}

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

import shapeless._

/**
  * Created by alonsodomin on 29/08/2016.
  */
abstract class CronDateTimeTestKit[DateTime <: AnyRef : DateTimeAdapter]
  extends PropSpec with TableDrivenPropertyChecks with Matchers { this: ExtensionsTestKitBase[DateTime] =>

  val onlyTuesdaysAt12 = CronExpr(
    ConstNode[Second](0),
    ConstNode[Minute](0),
    ConstNode[Hour](12),
    EachNode[DayOfMonth],
    EachNode[Month],
    ConstNode[DayOfWeek](1)
  )
  val onlySundays = CronExpr(
    ConstNode[Second](0),
    EachNode[Minute],
    EachNode[Hour],
    EachNode[DayOfMonth],
    EachNode[Month],
    ConstNode[DayOfWeek](6)
  )

  lazy val samples = Table(
    ("expr", "from", "stepSize", "expected"),
    (onlyTuesdaysAt12, createDateTime(0, 0, 0, 1, 8, 0), 1, createDateTime(0, 0, 12, 2, 8, 1)),
    (onlySundays, createDateTime(0, 0, 0, 1, 8, 0), 1, createDateTime(0, 1, 0, 7, 8, 6))
  )

  property("step") {
    forAll(samples) { (expr: CronExpr, initial: DateTime, stepSize: Int, expected: DateTime) =>
      val extCronExpr = new CronDateTimeOps[DateTime](expr) { }
      val returnedDateTime = extCronExpr.step(initial, stepSize)

      returnedDateTime shouldBe defined
      returnedDateTime.foreach { _ shouldBe expected }
    }
  }

}
