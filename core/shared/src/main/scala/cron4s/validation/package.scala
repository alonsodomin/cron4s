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

import cats.data.NonEmptyList

import cron4s.expr.CronExpr

/**
  * Created by alonsodomin on 30/08/2016.
  */
package object validation {

  def validateCron(expr: CronExpr): Either[InvalidCron, CronExpr] = {
    val dayFieldError = validateDayFields(expr)
    val fieldErrors = expr.raw.map(ops.validate).toList.flatten

    NonEmptyList.fromList[ValidationError](fieldErrors)
      .map(list => dayFieldError.fold(list)(_ :: list))
      .map(errs => Left(InvalidCron(errs)))
      .getOrElse(Right(expr))
  }

  private def validateDayFields(expr: CronExpr) = {
    def errorMsg(fieldExpr: String) =
      s"Fields ${CronField.DayOfMonth} and ${CronField.DayOfWeek} can't both have the expression: $fieldExpr"

    val dayOfMonth = expr.field[CronField.DayOfMonth].toString
    val dayOfWeek  = expr.field[CronField.DayOfWeek].toString

    def checkValue(value: String) = if (dayOfMonth == value && dayOfWeek == value) {
      Some(InvalidFieldCombination(errorMsg(value)))
    } else None

    checkValue("?") orElse checkValue("*")
  }

}
