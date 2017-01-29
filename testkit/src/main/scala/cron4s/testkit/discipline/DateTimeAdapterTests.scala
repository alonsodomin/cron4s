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

package cron4s.testkit.discipline

import cron4s.CronField
import cron4s.testkit.laws.DateTimeAdapterLaws
import org.scalacheck._
import Prop._
import cron4s.datetime.DateTimeAdapter
import cron4s.testkit.CronFieldValue
import org.typelevel.discipline.Laws

import scalaz.Equal

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait DateTimeAdapterTests[DateTime <: AnyRef] extends Laws {
  def laws: DateTimeAdapterLaws[DateTime]

  def dateTimeAdapter[F <: CronField](implicit
    arbDateTime: Arbitrary[DateTime],
    arbFieldValue: Arbitrary[CronFieldValue[F]]
  ): RuleSet = new DefaultRuleSet(
    name = "dateTimeAdapter",
    parent = None,
    "immutability" -> forAll(laws.immutability[F] _),
    "settable" -> forAll(laws.settable[F] _)
  )

}

object DateTimeAdapterTests {

  def apply[DateTime <: AnyRef](implicit
      adapterEv: DateTimeAdapter[DateTime],
      eqEv: Equal[DateTime]
  ): DateTimeAdapterTests[DateTime] =
    new DateTimeAdapterTests[DateTime] {
      val laws: DateTimeAdapterLaws[DateTime] = DateTimeAdapterLaws[DateTime]
    }

}
