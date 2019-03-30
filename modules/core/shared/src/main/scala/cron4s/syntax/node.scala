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

package cron4s
package syntax

import cron4s.CronField
import cron4s.datetime.{IsDateTime, DateTimeNode}

private[syntax] class DateTimeNodeOps[E[_ <: CronField], F <: CronField](
    self: E[F],
    tc: DateTimeNode[E, F]
) {

  def matchesIn[DateTime](dt: DateTime)(implicit DT: IsDateTime[DateTime]): Boolean =
    tc.matchesIn(self, DT)(dt)

  def nextIn[DateTime](dateTime: DateTime)(implicit DT: IsDateTime[DateTime]): Option[DateTime] =
    tc.nextIn(self, DT)(dateTime)

  def prevIn[DateTime](dateTime: DateTime)(implicit DT: IsDateTime[DateTime]): Option[DateTime] =
    tc.prevIn(self, DT)(dateTime)

  def stepIn[DateTime](dateTime: DateTime, step: Int)(
      implicit DT: IsDateTime[DateTime]
  ): Either[StepError, DateTime] =
    tc.stepIn(self, DT)(dateTime, step)

}

private[syntax] trait DateTimeNodeSyntax {

  implicit def toDateTimeNodeOps[E[_ <: CronField], F <: CronField, DateTime](
      target: E[F]
  )(implicit tc0: DateTimeNode[E, F]): DateTimeNodeOps[E, F] =
    new DateTimeNodeOps[E, F](target, tc0)

}

object node extends DateTimeNodeSyntax
