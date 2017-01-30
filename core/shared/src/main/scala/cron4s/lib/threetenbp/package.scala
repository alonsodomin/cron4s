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

package cron4s.lib

import cron4s.datetime.DateTimeAdapter

import org.threeten.bp._
import org.threeten.bp.temporal.Temporal

import scalaz.Equal

/**
  * Created by alonsodomin on 11/12/2016.
  */
package object threetenbp {

  implicit val localDateEq      : Equal[LocalDate]      = Equal.equalA[LocalDate]
  implicit val localTimeEq      : Equal[LocalTime]      = Equal.equalA[LocalTime]
  implicit val localDateTimeEq  : Equal[LocalDateTime]  = Equal.equalA[LocalDateTime]
  implicit val zonedDateTimeEq  : Equal[ZonedDateTime]  = Equal.equalA[ZonedDateTime]
  implicit val offsetDateTimeEq : Equal[OffsetDateTime] = Equal.equalA[OffsetDateTime]

  implicit def jsr310Adapter[DT <: Temporal]: DateTimeAdapter[DT] = new JSR310Adapter[DT]

}
