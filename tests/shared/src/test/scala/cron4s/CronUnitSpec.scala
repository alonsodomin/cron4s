package cron4s

import cron4s.testkit.discipline.HasCronFieldTests
import cron4s.testkit.gen.ArbitraryCronUnits
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

class CronUnitSpec extends FunSuite with Discipline with ArbitraryCronUnits {
  import CronField._

  checkAll("CronUnit[Second]", HasCronFieldTests[CronUnit, Second.type].hasCronField)
  checkAll("CronUnit[Minute]", HasCronFieldTests[CronUnit, Minute.type].hasCronField)
  checkAll("CronUnit[Hour]", HasCronFieldTests[CronUnit, Hour.type].hasCronField)
  checkAll("CronUnit[DayOfMonth]", HasCronFieldTests[CronUnit, DayOfMonth.type].hasCronField)
  checkAll("CronUnit[Month]", HasCronFieldTests[CronUnit, Month.type].hasCronField)
  checkAll("CronUnit[DayOfWeek]", HasCronFieldTests[CronUnit, DayOfWeek.type].hasCronField)

}
