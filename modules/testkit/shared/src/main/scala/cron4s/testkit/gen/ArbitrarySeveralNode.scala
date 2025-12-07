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
import cron4s.expr.SeveralNode

import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitrarySeveralNode extends NodeGenerators {
  implicit lazy val arbitrarySeveralSecond: Arbitrary[SeveralNode[Second]] = Arbitrary(
    severalGen[Second]
  )
  implicit lazy val arbitrarySeveralMinute: Arbitrary[SeveralNode[Minute]] = Arbitrary(
    severalGen[Minute]
  )
  implicit lazy val arbitrarySeveralHour: Arbitrary[SeveralNode[Hour]] = Arbitrary(severalGen[Hour])
  implicit lazy val arbitrarySeveralDayOfMonth: Arbitrary[SeveralNode[DayOfMonth]] = Arbitrary(
    severalGen[DayOfMonth]
  )
  implicit lazy val arbitrarySeveralMonth: Arbitrary[SeveralNode[Month]] = Arbitrary(
    severalGen[Month]
  )
  implicit lazy val arbitrarySeveralDayOfWeek: Arbitrary[SeveralNode[DayOfWeek]] = Arbitrary(
    severalGen[DayOfWeek]
  )
  implicit lazy val arbitrarySeveralYear: Arbitrary[SeveralNode[Year]] = Arbitrary(
    severalGen[Year]
  )
}
