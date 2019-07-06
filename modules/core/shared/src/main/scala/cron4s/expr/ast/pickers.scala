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
package expr
package ast

import cron4s.datetime._

sealed trait CronPicker[F <: CronField] extends HasCronUnit[F] {
  def pickFrom[DT](dateTime: DT)(implicit DT: IsDateTime[DT]): Either[ExprError, DT]
}
case object LastDayOfMonth extends CronPicker[CronField.DayOfMonth] {
  def unit: CronUnit[CronField.DayOfMonth] = CronUnit.DaysOfMonth

  def pickFrom[DT](dateTime: DT)(implicit DT: IsDateTime[DT]): Either[ExprError, DT] =
    for {
      value  <- DT.last(dateTime, unit.field)
      result <- DT.set(dateTime, unit.field, value)
    } yield result
}
case class NthDayOfWeek(nth: Int) extends CronPicker[CronField.DayOfWeek] {
  def unit: CronUnit[CronField.DayOfWeek] = CronUnit.DaysOfWeek

  def pickFrom[DT](dateTime: DT)(implicit DT: IsDateTime[DT]): Either[ExprError, DT] = ???
}
case class NthDayOfMonth(nth: Int) extends CronPicker[CronField.DayOfMonth] {
  def unit: CronUnit[CronField.DayOfMonth] = CronUnit.DaysOfMonth

  def pickFrom[DT](dateTime: DT)(implicit DT: IsDateTime[DT]): Either[ExprError, DT] = ???
}
case class NthDayOnMthWeek(nth: Int, mth: Int) extends CronPicker[CronField.DayOfMonth] {
  def unit: CronUnit[CronField.DayOfMonth] = CronUnit.DaysOfMonth

  def pickFrom[DT](dateTime: DT)(implicit DT: IsDateTime[DT]): Either[ExprError, DT] = ???
}
