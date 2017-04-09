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

package cron4s.lib.threetenbp

import cron4s.CronUnit
import cron4s.testkit.DateTimeTestKitBase

import org.scalacheck.{Arbitrary, Gen}

import org.threeten.bp._

/**
  * Created by alonsodomin on 29/08/2016.
  */

trait ThreeTenBPLocalDateTestBase extends DateTimeTestKitBase[LocalDate] {
  import CronUnit._

  override implicit lazy val arbitraryDateTime: Arbitrary[LocalDate] = Arbitrary {
    for {
      year       <- yearGen
      yearMonth  <- Gen.choose(Months.min, Months.max).map(YearMonth.of(year, _))
      dayOfMonth <- Gen.choose(DaysOfMonth.min, yearMonth.lengthOfMonth)
    } yield LocalDate.of(year, yearMonth.getMonthValue, dayOfMonth)
  }

  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, year: Int): LocalDate =
    LocalDate.of(year, month, dayOfMonth)

}

trait ThreeTenBPLocalTimeTestBase extends DateTimeTestKitBase[LocalTime] {
  import CronUnit._

  override implicit lazy val arbitraryDateTime: Arbitrary[LocalTime] = Arbitrary {
    for {
      second     <- Gen.choose(Seconds.min, Seconds.max)
      minute     <- Gen.choose(Minutes.min, Minutes.max)
      hour       <- Gen.choose(Hours.min, Hours.max)
    } yield LocalTime.of(hour, minute, second)
  }

  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, year: Int): LocalTime =
    LocalTime.of(hours, minutes, seconds)

}

trait ThreeTenBPLocalDateTimeTestBase extends DateTimeTestKitBase[LocalDateTime] {
  import CronUnit._

  override implicit lazy val arbitraryDateTime: Arbitrary[LocalDateTime] = Arbitrary {
    for {
      second     <- Gen.choose(Seconds.min, Seconds.max)
      minute     <- Gen.choose(Minutes.min, Minutes.max)
      hour       <- Gen.choose(Hours.min, Hours.max)
      year       <- yearGen
      yearMonth  <- Gen.choose(Months.min, Months.max).map(YearMonth.of(year, _))
      dayOfMonth <- Gen.choose(DaysOfMonth.min, yearMonth.lengthOfMonth)
    } yield LocalDateTime.of(year, yearMonth.getMonthValue, dayOfMonth, hour, minute, second)
  }

  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, year: Int): LocalDateTime =
    LocalDateTime.of(year, month, dayOfMonth, hours, minutes, seconds)

}

trait ThreeTenBPZonedDateTimeTestBase extends DateTimeTestKitBase[ZonedDateTime] {
  import CronUnit._

  override implicit lazy val arbitraryDateTime: Arbitrary[ZonedDateTime] = Arbitrary {
    for {
      second     <- Gen.choose(Seconds.min, Seconds.max)
      minute     <- Gen.choose(Minutes.min, Minutes.max)
      hour       <- Gen.choose(Hours.min, Hours.max)
      year       <- yearGen
      yearMonth  <- Gen.choose(Months.min, Months.max).map(YearMonth.of(year, _))
      dayOfMonth <- Gen.choose(DaysOfMonth.min, yearMonth.lengthOfMonth)
    } yield ZonedDateTime.of(year, yearMonth.getMonthValue, dayOfMonth, hour, minute, second, 0, ZoneOffset.UTC)
  }

  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, year: Int): ZonedDateTime =
    ZonedDateTime.of(year, month, dayOfMonth, hours, minutes, seconds, 0, ZoneOffset.UTC)
}

trait ThreeTenBPOffsetDateTimeTestBase extends DateTimeTestKitBase[OffsetDateTime] {
  import CronUnit._

  override implicit lazy val arbitraryDateTime: Arbitrary[OffsetDateTime] = Arbitrary {
    for {
      second     <- Gen.choose(Seconds.min, Seconds.max)
      minute     <- Gen.choose(Minutes.min, Minutes.max)
      hour       <- Gen.choose(Hours.min, Hours.max)
      year       <- yearGen
      yearMonth  <- Gen.choose(Months.min, Months.max).map(YearMonth.of(year, _))
      dayOfMonth <- Gen.choose(DaysOfMonth.min, yearMonth.lengthOfMonth)
    } yield OffsetDateTime.of(year, yearMonth.getMonthValue, dayOfMonth, hour, minute, second, 0, ZoneOffset.UTC)
  }

  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, year: Int): OffsetDateTime =
    OffsetDateTime.of(year, month, dayOfMonth, hours, minutes, seconds, 0, ZoneOffset.UTC)
}
