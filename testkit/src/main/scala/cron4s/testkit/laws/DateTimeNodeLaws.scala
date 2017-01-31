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

package cron4s.testkit.laws

import cron4s.CronField
import cron4s.datetime.{DateTimeAdapter, DateTimeNode}
import cron4s.expr.Expr
import cron4s.testkit._
import cron4s.syntax.node._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait DateTimeNodeLaws[E[_ <: CronField], F <: CronField, DateTime] {
  implicit def adapter: DateTimeAdapter[DateTime]
  implicit def expr: Expr[E, F]
  implicit def TC: DateTimeNode[E, F]

  def matchable(e: E[F], dt: DateTime): IsEqual[Boolean] = {
    val fieldVal = adapter.get(dt, expr.unit(e).field)
    e.matchesIn(dt) <-> fieldVal.exists(expr.matches(e)(_))
  }

  def forward(e: E[F], from: DateTime): IsEqual[Option[DateTime]] =
    e.nextIn(from) <-> e.stepIn(from, 1)

  def backwards(e: E[F], from: DateTime): IsEqual[Option[DateTime]] =
    e.prevIn(from) <-> e.stepIn(from, -1)

}

object DateTimeNodeLaws {

  def apply[E[_ <: CronField], F <: CronField, DateTime](implicit
      adapter0: DateTimeAdapter[DateTime],
      expr0: Expr[E, F],
      TC0: DateTimeNode[E, F]
  ): DateTimeNodeLaws[E, F, DateTime] =
    new DateTimeNodeLaws[E, F, DateTime] {
      implicit val adapter = adapter0
      implicit val expr = expr0
      implicit val TC = TC0
    }

}
