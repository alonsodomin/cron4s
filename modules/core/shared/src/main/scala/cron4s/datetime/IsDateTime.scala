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
package datetime

import cron4s.CronField

import scala.annotation.implicitNotFound

/**
  * Bridge adapter between specific date-time libraries and expression support
  *
  * @author Antonio Alonso Dominguez
  */
@implicitNotFound(
  "Type ${DateTime} is not supported on current scope. You may be missing some imports, check the documentation to know more."
)
trait IsDateTime[DateTime] {

  def plus(dateTime: DateTime, amount: Int, unit: DateTimeUnit): Option[DateTime]

  /**
    * List of the fields supported by this date time representation
    *
    * @param dateTime the date time representation
    * @return list of the supported fields
    */
  def supportedFields(dateTime: DateTime): List[CronField]

  /**
    * Getter access for a specific field in a date-time
    *
    * @param dateTime a date-time
    * @param field a CronField
    * @tparam F the CronField type
    * @return value of the field
    */
  def get[F <: CronField](dateTime: DateTime, field: F): Either[DateTimeStepError, Int]

  /**
    * Setter access for a specific field in a date-time
    *
    * @param dateTime a date-time
    * @param field a CronField
    * @param value new value for the field
    * @tparam F the CronField type
    * @return a new date-time with the given field set to the new value
    */
  def set[F <: CronField](
      dateTime: DateTime,
      field: F,
      value: Int
  ): Either[DateTimeStepError, DateTime]

}

object IsDateTime {
  @inline def apply[DateTime](implicit ev: IsDateTime[DateTime]): IsDateTime[DateTime] = ev
}
