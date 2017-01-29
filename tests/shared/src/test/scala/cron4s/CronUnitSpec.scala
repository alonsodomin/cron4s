/*
 * Copyright 2017 Antonio Alonso Dominguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cron4s

import cron4s.testkit.discipline.EnumeratedTests
import cron4s.testkit.gen.ArbitraryCronUnits

import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

class CronUnitSpec extends FunSuite with Discipline with ArbitraryCronUnits {
  import CronField._

  checkAll("CronUnit[Second]", EnumeratedTests[CronUnit[Second]].enumerated)
  checkAll("CronUnit[Minute]", EnumeratedTests[CronUnit[Minute]].enumerated)
  checkAll("CronUnit[Hour]", EnumeratedTests[CronUnit[Hour]].enumerated)
  checkAll("CronUnit[DayOfMonth]", EnumeratedTests[CronUnit[DayOfMonth]].enumerated)
  checkAll("CronUnit[Month]", EnumeratedTests[CronUnit[Month]].enumerated)
  checkAll("CronUnit[DayOfWeek]", EnumeratedTests[CronUnit[DayOfWeek]].enumerated)

}
