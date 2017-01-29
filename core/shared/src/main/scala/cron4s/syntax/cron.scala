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

import cron4s.datetime.DateTimeCron
import cron4s.types.Predicate

/**
  * Created by alonsodomin on 25/01/2017.
  */
private[syntax] class DateTimeCronOps[E, DateTime](self: E, tc: DateTimeCron[E, DateTime]) {

  def allOf: Predicate[DateTime] = tc.allOf(self)

  def anyOf: Predicate[DateTime] = tc.anyOf(self)

  def next(from: DateTime): Option[DateTime] = step(from, 1)

  def prev(from: DateTime): Option[DateTime] = step(from, -1)

  def step(from: DateTime, stepSize: Int): Option[DateTime] = tc.step(self)(from, stepSize)

}

private[syntax] trait DateTimeCronSyntax {

  implicit def toDateTimeCronOps[E, DateTime]
      (target: E)
      (implicit tc0: DateTimeCron[E, DateTime]): DateTimeCronOps[E, DateTime] =
    new DateTimeCronOps[E, DateTime](target, tc0)

}

object cron extends DateTimeCronSyntax