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

import cron4s.datetime.{DateTimeAdapter, DateTimeCron}

/**
  * Created by alonsodomin on 25/01/2017.
  */
private[syntax] class DateTimeCronOps[E](self: E, tc: DateTimeCron[E]) {

  def allOf[DateTime](dt: DateTime)(implicit adapter: DateTimeAdapter[DateTime]): Boolean =
    tc.allOf(self, adapter)(dt)

  def anyOf[DateTime](dt: DateTime)(implicit adapter: DateTimeAdapter[DateTime]): Boolean =
    tc.anyOf(self, adapter)(dt)

  def next[DateTime](from: DateTime)(implicit adapter: DateTimeAdapter[DateTime]): Option[DateTime] =
    step(from, 1)

  def prev[DateTime](from: DateTime)(implicit adapter: DateTimeAdapter[DateTime]): Option[DateTime] =
    step(from, -1)

  def step[DateTime](from: DateTime, stepSize: Int)(implicit adapter: DateTimeAdapter[DateTime]): Option[DateTime] =
    tc.step(self, adapter)(from, stepSize)

  def ranges: List[IndexedSeq[Int]] =
    tc.ranges(self)

}

private[syntax] trait DateTimeCronSyntax {

  implicit def toDateTimeCronOps[E, DateTime]
      (target: E)
      (implicit tc0: DateTimeCron[E]): DateTimeCronOps[E] =
    new DateTimeCronOps[E](target, tc0)

}

object cron extends DateTimeCronSyntax