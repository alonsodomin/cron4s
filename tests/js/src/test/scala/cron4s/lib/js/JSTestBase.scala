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

package cron4s.lib.js

import cron4s.CronUnit
import cron4s.testkit.DateTimeTestKitBase

import org.scalacheck.{Arbitrary, Gen}

import scala.scalajs.js.Date

/**
  * Created by alonsodomin on 02/09/2016.
  */
trait JSTestBase extends DateTimeTestKitBase[Date] {
  import CronUnit._

  implicit lazy val arbitraryDateTime = Arbitrary(for {
    seconds     <- Gen.choose(Seconds.min, Seconds.max)
    minutes     <- Gen.choose(Minutes.min, Minutes.max)
    hours       <- Gen.choose(Hours.min, Hours.max)
    month       <- Gen.choose(Months.min, Months.max)
    // Prevents choosing days in the 30-31 range, which cause non-deterministic results
    dayOfMonth  <- if (month == 2) Gen.choose(DaysOfMonth.min, 28)
                   else Gen.choose(DaysOfMonth.min, 30)
    year        <- yearGen
  } yield createDateTime(seconds, minutes, hours, dayOfMonth, month, year))

  protected def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, year: Int): Date =
    new Date(Date.UTC(year, month - 1, dayOfMonth, hours, minutes, seconds, ms = 0))

}
