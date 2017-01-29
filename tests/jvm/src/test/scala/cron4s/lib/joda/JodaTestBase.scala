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

package cron4s.lib.joda

import cron4s.CronUnit
import cron4s.testkit.DateTimeTestKitBase

import org.joda.time.{DateTime, YearMonth}

import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait JodaTestBase extends DateTimeTestKitBase[DateTime] {
  import CronUnit._

  final val Year = 2014

  override implicit lazy val arbitraryDateTime: Arbitrary[DateTime] = Arbitrary {
    for {
      second     <- Gen.choose(Seconds.min, Seconds.max)
      minute     <- Gen.choose(Minutes.min, Minutes.max)
      hour       <- Gen.choose(Hours.min, Hours.max)
      yearMonth  <- Gen.choose(Months.min, Months.max).map(new YearMonth(Year, _))
      dayOfMonth <- Gen.choose(DaysOfMonth.min, yearMonth.toLocalDate(1).dayOfMonth().getMaximumValue)
    } yield new DateTime(Year, yearMonth.getMonthOfYear, dayOfMonth, hour, minute, second)
  }

  protected def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): DateTime =
    new DateTime(Year, month, dayOfMonth, hours, minutes, seconds)

}
