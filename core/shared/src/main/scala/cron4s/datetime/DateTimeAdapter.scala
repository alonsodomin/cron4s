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

import cron4s.CronField

/**
  * Bridge adapter between specific date-time libraries and expression support
  *
  * @author Antonio Alonso Dominguez
  */
trait DateTimeAdapter[DateTime] {

  /**
    * Getter access for a specific field in a date-time
    *
    * @param dateTime a date-time
    * @param field a CronField
    * @tparam F the CronField type
    * @return value of the field
    */
  def get[F <: CronField](dateTime: DateTime, field: F): Option[Int]

  /**
    * Setter access for a specific field in a date-time
    *
    * @param dateTime a date-time
    * @param field a CronField
    * @param value new value for the field
    * @tparam F the CronField type
    * @return a new date-time with the given field set to the new value
    */
  def set[F <: CronField](dateTime: DateTime, field: F, value: Int): Option[DateTime]

}

object DateTimeAdapter {
  @inline def apply[DateTime](implicit adapter: DateTimeAdapter[DateTime]): DateTimeAdapter[DateTime] = adapter
}