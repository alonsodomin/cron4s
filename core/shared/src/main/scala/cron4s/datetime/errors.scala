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

package cron4s.datetime

import cats.Eq
import cron4s.CronField

sealed abstract class DateTimeError(msg: String) extends Exception(msg)
object DateTimeError {
  implicit val dateTimeErrorEq: Eq[DateTimeError] = Eq.fromUniversalEquals
}

final case class UnsupportedField(field: CronField)
    extends DateTimeError(s"Field $field is not supported")
final case class InvalidFieldValue(field: CronField, value: Int)
    extends DateTimeError(s"Value $value is not valid for field $field")
