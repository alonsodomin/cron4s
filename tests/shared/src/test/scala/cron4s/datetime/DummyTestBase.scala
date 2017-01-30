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

package cron4s.datetime

import cron4s.CronUnit
import cron4s.testkit.DateTimeTestKitBase

import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait DummyTestBase extends DateTimeTestKitBase[DummyDateTime] {
  import CronUnit._

  implicit lazy val arbitraryDateTime: Arbitrary[DummyDateTime] = Arbitrary(for {
    seconds     <- Gen.choose(Seconds.min, Seconds.max)
    minutes     <- Gen.choose(Minutes.min, Minutes.max)
    hours       <- Gen.choose(Hours.min, Hours.max)
    daysOfMonth <- Gen.choose(DaysOfMonth.min, DaysOfMonth.max)
    months      <- Gen.choose(Months.min, Months.max)
    daysOfWeek  <- Gen.choose(DaysOfWeek.min, DaysOfWeek.max)
  } yield createDateTime(seconds, minutes, hours, daysOfMonth, months, daysOfWeek))

  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): DummyDateTime =
    DummyDateTime(seconds, minutes, hours, dayOfMonth, month, dayOfWeek)
}
