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

import cats.Eq
import cats.laws.discipline._
import cats.implicits._

import cron4s.datetime.{IsDateTime, DateTimeCron}
import cron4s.testkit.laws.DateTimeCronLaws

import org.scalacheck.Prop._
import org.scalacheck._

import org.typelevel.discipline.Laws

/**
  * Created by alonsodomin on 29/01/2017.
  */
trait DateTimeCronTests[E, DateTime] extends Laws {
  def laws: DateTimeCronLaws[E, DateTime]

  def dateTimeCron(implicit
      arbE: Arbitrary[E],
      arbDateTime: Arbitrary[DateTime],
      dateTimeEq: Eq[DateTime]
  ): RuleSet =
    new DefaultRuleSet(
      name = "dateTimeCron",
      parent = None,
      "matchAll"        -> forAll(laws.matchAll _),
      "matchAny"        -> forAll(laws.matchAny _),
      "forwards"        -> forAll(laws.forwards _),
      "backwards"       -> forAll(laws.backwards _),
      "supportedFields" -> forAll(laws.supportedFieldsEquality _)
    )
}

object DateTimeCronTests {
  def apply[E, DateTime](implicit
      dtEv: IsDateTime[DateTime],
      cron: DateTimeCron[E]
  ): DateTimeCronTests[E, DateTime] =
    new DateTimeCronTests[E, DateTime] {
      val laws = DateTimeCronLaws[E, DateTime]
    }
}
