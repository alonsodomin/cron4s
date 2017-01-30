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

import java.time.temporal.{ChronoField, Temporal, TemporalField}
import java.time._

import cron4s.CronField
import cron4s.datetime.DateTimeAdapter

import scala.util.Try
import scalaz.Equal

/**
  * Created by alonsodomin on 11/12/2016.
  */
package object javatime {
  import CronField._

  implicit lazy val javaLocalDateEq      : Equal[LocalDate]      = Equal.equalA[LocalDate]
  implicit lazy val javaLocalTimeEq      : Equal[LocalTime]      = Equal.equalA[LocalTime]
  implicit lazy val javaLocalDateTimeEq  : Equal[LocalDateTime]  = Equal.equalA[LocalDateTime]
  implicit lazy val javaZonedDateTimeEq  : Equal[ZonedDateTime]  = Equal.equalA[ZonedDateTime]
  implicit lazy val javaOffsetDateTimeEq : Equal[OffsetDateTime] = Equal.equalA[OffsetDateTime]

  //implicit object JavaLocalDateAdapter extends JavaTimeAdapter[LocalDate]
  //implicit object JavaLocalDateTimeAdapter extends JavaTimeAdapter[LocalDateTime]
  //implicit object JavaZonedDateTimeAdapter extends JavaTimeAdapter[ZonedDateTime]

  private[this] def javaTimeAdapter[DT <: Temporal]: DateTimeAdapter[DT] = new JavaTimeAdapter[DT]

  implicit val javaLocalDateAdapter      : DateTimeAdapter[LocalDate]      = javaTimeAdapter[LocalDate]
  implicit val javaLocalTimeAdapter      : DateTimeAdapter[LocalTime]      = javaTimeAdapter[LocalTime]
  /*
  implicit lazy val javaLocalDateTimeAdapter  : DateTimeAdapter[LocalDateTime]  = javaTimeAdapter[LocalDateTime]
  implicit lazy val javaZonedDateTimeAdapter  : DateTimeAdapter[ZonedDateTime]  = javaTimeAdapter[ZonedDateTime]
  implicit lazy val javaOffsetDateTimeAdapter : DateTimeAdapter[OffsetDateTime] = javaTimeAdapter[OffsetDateTime]*/

}