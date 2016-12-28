package cron4s

import cron4s.testkit.discipline.EnumeratedTests
import cron4s.testkit.gen.ArbitraryCronUnits

import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

class CronUnitSpec extends FunSuite with Discipline with ArbitraryCronUnits {
  import CronField._

  checkAll("CronUnit[Second]", EnumeratedTests[CronUnit, Second].enumerated)
  checkAll("CronUnit[Minute]", EnumeratedTests[CronUnit, Minute].enumerated)
  checkAll("CronUnit[Hour]", EnumeratedTests[CronUnit, Hour].enumerated)
  checkAll("CronUnit[DayOfMonth]", EnumeratedTests[CronUnit, DayOfMonth].enumerated)
  checkAll("CronUnit[Month]", EnumeratedTests[CronUnit, Month].enumerated)
  checkAll("CronUnit[DayOfWeek]", EnumeratedTests[CronUnit, DayOfWeek].enumerated)

}
