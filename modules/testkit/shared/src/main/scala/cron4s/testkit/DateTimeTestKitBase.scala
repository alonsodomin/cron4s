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

import java.time.YearMonth

import cron4s.CronUnit._

import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait DateTimeTestKitBase[DateTime] {
  protected def genDateTime: Gen[DateTime] = for {
    second     <- Gen.choose(Seconds.min, Seconds.max)
    minute     <- Gen.choose(Minutes.min, Minutes.max)
    hour       <- Gen.choose(Hours.min, Hours.max)
    year       <- Gen.choose(Years.min,Years.max)
    yearMonth  <- Gen.choose(Months.min, Months.max).map(YearMonth.of(year, _))
    dayOfMonth <- Gen.choose(DaysOfMonth.min, yearMonth.lengthOfMonth())
  } yield createDateTime(second, minute, hour, dayOfMonth, yearMonth.getMonthValue, year)

  implicit final lazy val arbitraryDateTime: Arbitrary[DateTime] = Arbitrary(genDateTime)

  protected def createDateTime(
      seconds: Int,
      minutes: Int,
      hours: Int,
      dayOfMonth: Int,
      month: Int,
      year: Int
  ): DateTime
}
