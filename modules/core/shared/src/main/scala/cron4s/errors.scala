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

import cats.Show
import cats.data.NonEmptyList
import cats.syntax.show._

/**
  * Created by alonsodomin on 30/08/2016.
  */
sealed abstract class Error(description: String) extends Exception(description)

object Error {
  implicit val errorShow: Show[Error] = Show.show(_.getMessage)
}

final case class ParseFailed(msg: String, found: String, position: Int)
    extends Error(s"$msg at position $position but found '$found'")

sealed trait ValidationError
object ValidationError {
  implicit val validationErrorShow: Show[ValidationError] = Show.show {
    case e: InvalidField            => e.show
    case e: InvalidFieldCombination => e.show
  }
}

final case class InvalidCron(reason: NonEmptyList[ValidationError])
    extends Error(reason.toList.map(_.show).mkString(", "))

final case class InvalidField(field: CronField, msg: String) extends ValidationError
object InvalidField {
  implicit val invalidFieldShow: Show[InvalidField] = Show.show { err =>
    s"${err.field}: ${err.msg}"
  }
}

final case class InvalidFieldCombination(msg: String) extends ValidationError
object InvalidFieldCombination {
  implicit val invalidFieldCombinationShow: Show[InvalidFieldCombination] =
    Show.show(_.msg)
}

sealed abstract class StepError(msg: String) extends Error(msg)
case class StepSizeOutOfRange(stepSize: Int)
    extends StepError(s"Step size $stepSize is out of range")

sealed abstract class DateTimeStepError(msg: String) extends StepError(msg)
final case class UnsupportedField(field: CronField)
    extends DateTimeStepError(s"Field $field is not supported")
final case class InvalidFieldValue(field: CronField, value: Int)
    extends DateTimeStepError(s"Value $value is not valid for field $field")
