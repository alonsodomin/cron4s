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

import cron4s.CronField

import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by domingueza on 31/01/2017.
  */
trait ArbitraryCronField {
  import CronField._

  implicit lazy val arbitrarySecond: Arbitrary[Second] = Arbitrary(
    Gen.const(Second: Second))
  implicit lazy val arbitraryMinute: Arbitrary[Minute] = Arbitrary(
    Gen.const(Minute: Minute))
  implicit lazy val arbitraryHour: Arbitrary[Hour] = Arbitrary(
    Gen.const(Hour: Hour))
  implicit lazy val arbitraryDayOfMonth: Arbitrary[DayOfMonth] = Arbitrary(
    Gen.const(DayOfMonth: DayOfMonth))
  implicit lazy val arbitraryMonth: Arbitrary[Month] = Arbitrary(
    Gen.const(Month: Month))
  implicit lazy val arbitraryDayOfWeek: Arbitrary[DayOfWeek] = Arbitrary(
    Gen.const(DayOfWeek: DayOfWeek))

}
