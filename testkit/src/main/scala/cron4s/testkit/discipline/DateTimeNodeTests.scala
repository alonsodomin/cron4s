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

import cron4s.CronField
import cron4s.datetime.IsDateTime
import cron4s.expr.FieldExpr
import cron4s.testkit.laws.DateTimeNodeLaws

import org.scalacheck.Prop._
import org.scalacheck._
import org.typelevel.discipline.Laws

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait DateTimeNodeTests[E[_ <: CronField], F <: CronField, DateTime] extends Laws {
  def laws: DateTimeNodeLaws[E, F, DateTime]

  def dateTime(implicit
               arbNode: Arbitrary[E[F]],
               arbDateTime: Arbitrary[DateTime],
               dateTimeEq: Eq[DateTime]
  ): RuleSet = new DefaultRuleSet(
    name = "dateTimeNode",
    parent = None,
    "forward" -> forAll(laws.forward _),
    "backwards" -> forAll(laws.backwards _),
    "matchable" -> forAll(laws.matchable _)
  )

}

object DateTimeNodeTests {

  def apply[E[_ <: CronField], F <: CronField, DateTime](implicit
    dtEv: IsDateTime[DateTime],
    expr: FieldExpr[E, F]
  ): DateTimeNodeTests[E, F, DateTime] =
    new DateTimeNodeTests[E, F, DateTime] {
      val laws = DateTimeNodeLaws[E, F, DateTime]
    }

}
