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
import cron4s.spi.{DateTimeAdapter, NodeDateTimeOps}
import cron4s.testkit._
import cron4s.types._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait NodeDateTimeLaws[E[_ <: CronField], F <: CronField, DateTime] {
  implicit def adapter: DateTimeAdapter[DateTime]
  implicit def ev: Expr[E, F]

  def matchable(expr: E[F], dt: DateTime): IsEqual[Boolean] = {
    val fieldVal = adapter.get(dt, ev.unit(expr).field)
    val exExpr = new NodeDateTimeOps[E, F, DateTime](expr, adapter, ev) {}
    exExpr.matchesIn(dt) <-> fieldVal.exists(ev.matches(expr)(_))
  }

  def forward(expr: E[F], from: DateTime): IsEqual[Option[DateTime]] = {
    val exExpr = new NodeDateTimeOps[E, F, DateTime](expr, adapter, ev) {}
    exExpr.nextIn(from) <-> exExpr.stepIn(from, 1)
  }

  def backwards(expr: E[F], from: DateTime): IsEqual[Option[DateTime]] = {
    val exExpr = new NodeDateTimeOps[E, F, DateTime](expr, adapter, ev) {}
    exExpr.prevIn(from) <-> exExpr.stepIn(from, -1)
  }

}

object NodeDateTimeLaws {

  def apply[E[_ <: CronField], F <: CronField, DateTime](implicit
      adapterEv: DateTimeAdapter[DateTime],
      exprEv: Expr[E, F]
  ): NodeDateTimeLaws[E, F, DateTime] =
    new NodeDateTimeLaws[E, F, DateTime] {
      implicit val adapter = adapterEv
      implicit val ev = exprEv
    }

}
