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

import cron4s.{CronField, CronUnit}
import cron4s.testkit.CronFieldValue
import cron4s.base.Enumerated
import cron4s.syntax.enumerated._

import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait ArbitraryCronFieldValues {
  import CronField._
  import CronUnit._

  def cronFieldValueGen[F <: CronField](
      unit: CronUnit[F]
  )(implicit ev: Enumerated[CronUnit[F]]): Gen[CronFieldValue[F]] = {
    val valueGen = unit match {
      case DaysOfMonth => Gen.choose(1, 28)
      case _           => Gen.choose(unit.min, unit.max)
    }

    valueGen.map(v => CronFieldValue(unit.field, v))
  }

  implicit lazy val arbitrarySecondValue: Arbitrary[CronFieldValue[Second]] =
    Arbitrary(cronFieldValueGen(Seconds))
  implicit lazy val arbitraryMinuteValue: Arbitrary[CronFieldValue[Minute]] =
    Arbitrary(cronFieldValueGen(Minutes))
  implicit lazy val arbitraryHourValue: Arbitrary[CronFieldValue[Hour]] =
    Arbitrary(cronFieldValueGen(Hours))
  implicit lazy val arbitraryDayOfMonthValue: Arbitrary[CronFieldValue[DayOfMonth]] =
    Arbitrary(cronFieldValueGen(DaysOfMonth))
  implicit lazy val arbitraryMonthValue: Arbitrary[CronFieldValue[Month]] =
    Arbitrary(cronFieldValueGen(Months))
  implicit lazy val arbitraryDayOfWeekValue: Arbitrary[CronFieldValue[DayOfWeek]] =
    Arbitrary(cronFieldValueGen(DaysOfWeek))

}
