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

package cron4s.testkit

import cats.Eq

import cron4s.datetime.IsDateTime
import cron4s.CronField
import cron4s.testkit.discipline.IsDateTimeTests
import cron4s.testkit.gen.{ArbitraryCronField, ArbitraryCronFieldValues}

/**
  * Created by alonsodomin on 29/08/2016.
  */
abstract class IsDateTimeTestKit[DateTime: IsDateTime: Eq](name: String)
    extends Cron4sLawSuite with ArbitraryCronFieldValues with ArbitraryCronField
    with DateTimeTestKitBase[DateTime] {
  import CronField._

  checkAll(s"IsDateTime[$name, Second]", IsDateTimeTests[DateTime].dateTime[Second])
  checkAll(s"IsDateTime[$name, Minute]", IsDateTimeTests[DateTime].dateTime[Minute])
  checkAll(s"IsDateTime[$name, Hour]", IsDateTimeTests[DateTime].dateTime[Hour])
  checkAll(s"IsDateTime[$name, DayOfMonth]", IsDateTimeTests[DateTime].dateTime[DayOfMonth])
  checkAll(s"IsDateTime[$name, Month]", IsDateTimeTests[DateTime].dateTime[Month])
  checkAll(s"IsDateTime[$name, DayOfWeek]", IsDateTimeTests[DateTime].dateTime[DayOfWeek])
}
