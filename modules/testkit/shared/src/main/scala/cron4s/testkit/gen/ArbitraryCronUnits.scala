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

package cron4s.testkit.gen

import cron4s.CronField._
import cron4s.CronUnit

import org.scalacheck._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryCronUnits {
  implicit lazy val arbitrarySecondsUnit: Arbitrary[CronUnit[Second]] = Arbitrary(
    Gen.const(CronUnit[Second])
  )
  implicit lazy val arbitraryMinutesUnit: Arbitrary[CronUnit[Minute]] = Arbitrary(
    Gen.const(CronUnit[Minute])
  )
  implicit lazy val arbitraryHoursUnit: Arbitrary[CronUnit[Hour]] = Arbitrary(
    Gen.const(CronUnit[Hour])
  )
  implicit lazy val arbitraryDaysOfMonthUnit: Arbitrary[CronUnit[DayOfMonth]] = Arbitrary(
    Gen.const(CronUnit[DayOfMonth])
  )
  implicit lazy val arbitraryMonthsUnit: Arbitrary[CronUnit[Month]] = Arbitrary(
    Gen.const(CronUnit[Month])
  )
  implicit lazy val arbitraryDaysOfWeekUnit: Arbitrary[CronUnit[DayOfWeek]] = Arbitrary(
    Gen.const(CronUnit[DayOfWeek])
  )
  implicit lazy val arbitraryYearsUnit: Arbitrary[CronUnit[Year]] = Arbitrary(
    Gen.const(CronUnit[Year])
  )
}
