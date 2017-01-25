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

package cron4s.syntax

import cron4s.CronField
import cron4s.spi.DateTimeNode
import cron4s.types.Predicate

private[syntax] class DateTimeNodeOps[E[_ <: CronField], F <: CronField, DateTime](self: E[F], tc: DateTimeNode[E, F, DateTime]) {

  def matchesIn: Predicate[DateTime] = tc.matchesIn(self)

  def nextIn(dateTime: DateTime): Option[DateTime] = tc.nextIn(self)(dateTime)

  def prevIn(dateTime: DateTime): Option[DateTime] = tc.prevIn(self)(dateTime)

  def stepIn(dateTime: DateTime, step: Int): Option[DateTime] = tc.stepIn(self)(dateTime, step)

}

private[syntax] trait DateTimeNodeSyntax {

  implicit def toDateTimeNodeOps[E[_ <: CronField], F <: CronField, DateTime]
      (target: E[F])
      (implicit tc0: DateTimeNode[E, F, DateTime]): DateTimeNodeOps[E, F, DateTime] =
    new DateTimeNodeOps[E, F, DateTime](target, tc0)

}

object node extends DateTimeNodeSyntax