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

import org.joda.time._

import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait JodaDateTimeTestBase extends DateTimeTestKitBase[DateTime] {
  import CronUnit._

  override implicit lazy val arbitraryDateTime: Arbitrary[DateTime] = Arbitrary {
    for {
      second     <- Gen.choose(Seconds.min, Seconds.max)
      minute     <- Gen.choose(Minutes.min, Minutes.max)
      hour       <- Gen.choose(Hours.min, Hours.max)
      year       <- yearGen
      yearMonth  <- Gen.choose(Months.min, Months.max).map(new YearMonth(year, _))
      dayOfMonth <- Gen.choose(DaysOfMonth.min, yearMonth.toLocalDate(1).dayOfMonth().getMaximumValue)
    } yield new DateTime(year, yearMonth.getMonthOfYear, dayOfMonth, hour, minute, second)
  }

  protected def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, year: Int): DateTime =
    new DateTime(year, month, dayOfMonth, hours, minutes, seconds)

}

trait JodaLocalDateTestBase extends DateTimeTestKitBase[LocalDate] {
  import CronUnit._

  override implicit lazy val arbitraryDateTime: Arbitrary[LocalDate] = Arbitrary {
    for {
      year       <- yearGen
      yearMonth  <- Gen.choose(Months.min, Months.max).map(new YearMonth(year, _))
      dayOfMonth <- Gen.choose(DaysOfMonth.min, yearMonth.toLocalDate(1).dayOfMonth().getMaximumValue)
    } yield new LocalDate(year, yearMonth.getMonthOfYear, dayOfMonth)
  }

  protected def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, year: Int): LocalDate =
    new LocalDate(year, month, dayOfMonth)

}

trait JodaLocalTimeTestBase extends DateTimeTestKitBase[LocalTime] {
  import CronUnit._

  override implicit lazy val arbitraryDateTime: Arbitrary[LocalTime] = Arbitrary {
    for {
      second     <- Gen.choose(Seconds.min, Seconds.max)
      minute     <- Gen.choose(Minutes.min, Minutes.max)
      hour       <- Gen.choose(Hours.min, Hours.max)
    } yield new LocalTime(hour, minute, second)
  }

  protected def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, year: Int): LocalTime =
    new LocalTime(hours, minutes, seconds)

}

trait JodaLocalDateTimeTestBase extends DateTimeTestKitBase[LocalDateTime] {
  import CronUnit._

  override implicit lazy val arbitraryDateTime: Arbitrary[LocalDateTime] = Arbitrary {
    for {
      second     <- Gen.choose(Seconds.min, Seconds.max)
      minute     <- Gen.choose(Minutes.min, Minutes.max)
      hour       <- Gen.choose(Hours.min, Hours.max)
      year       <- yearGen
      yearMonth  <- Gen.choose(Months.min, Months.max).map(new YearMonth(year, _))
      dayOfMonth <- Gen.choose(DaysOfMonth.min, yearMonth.toLocalDate(1).dayOfMonth().getMaximumValue)
    } yield new LocalDateTime(year, yearMonth.getMonthOfYear, dayOfMonth, hour, minute, second)
  }

  protected def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, year: Int): LocalDateTime =
    new LocalDateTime(year, month, dayOfMonth, hours, minutes, seconds)

}