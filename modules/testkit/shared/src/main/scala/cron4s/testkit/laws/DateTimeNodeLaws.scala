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

import cats.laws._
import cats.syntax.either._

import cron4s.CronField
import cron4s.datetime.{IsDateTime, DateTimeNode}
import cron4s.expr.FieldExpr
import cron4s.syntax.node._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait DateTimeNodeLaws[E[_ <: CronField], F <: CronField, DateTime] {
  implicit def DT: IsDateTime[DateTime]
  implicit def expr: FieldExpr[E, F]
  implicit def TC: DateTimeNode[E, F]

  def matchable(e: E[F], dt: DateTime): IsEq[Boolean] = {
    val fieldVal = DT.get(dt, expr.unit(e).field).toOption
    e.matchesIn(dt) <-> fieldVal.exists(expr.matches(e)(_))
  }

  def forward(e: E[F], from: DateTime): IsEq[Option[DateTime]] =
    e.nextIn(from) <-> e.stepIn(from, 1).toOption

  def backwards(e: E[F], from: DateTime): IsEq[Option[DateTime]] =
    e.prevIn(from) <-> e.stepIn(from, -1).toOption

}

object DateTimeNodeLaws {

  def apply[E[_ <: CronField], F <: CronField, DateTime](
      implicit
      dt0: IsDateTime[DateTime],
      expr0: FieldExpr[E, F],
      TC0: DateTimeNode[E, F]
  ): DateTimeNodeLaws[E, F, DateTime] =
    new DateTimeNodeLaws[E, F, DateTime] {
      implicit val DT   = dt0
      implicit val expr = expr0
      implicit val TC   = TC0
    }

}
