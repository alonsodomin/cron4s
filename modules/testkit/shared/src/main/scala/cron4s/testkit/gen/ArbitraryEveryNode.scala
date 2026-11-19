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
import cron4s.expr.EveryNode
import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryEveryNode extends NodeGenerators {
  implicit lazy val arbitraryEverySecond: Arbitrary[EveryNode[Second]] = Arbitrary(everyGen[Second])
  implicit lazy val arbitraryEveryMinute: Arbitrary[EveryNode[Minute]] = Arbitrary(everyGen[Minute])
  implicit lazy val arbitraryEveryHour: Arbitrary[EveryNode[Hour]]     = Arbitrary(everyGen[Hour])
  implicit lazy val arbitraryEveryDayOfMonth: Arbitrary[EveryNode[DayOfMonth]] = Arbitrary(
    everyGen[DayOfMonth]
  )
  implicit lazy val arbitraryEveryMonth: Arbitrary[EveryNode[Month]] = Arbitrary(everyGen[Month])
  implicit lazy val arbitraryEveryDayOfWeek: Arbitrary[EveryNode[DayOfWeek]] = Arbitrary(
    everyGen[DayOfWeek]
  )
  implicit lazy val arbitraryEveryYear: Arbitrary[EveryNode[Year]] = Arbitrary(everyGen[Year])
}
