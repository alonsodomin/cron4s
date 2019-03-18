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

import java.time._
import java.time.temporal.Temporal

import cats.{Eq, Show}

import cron4s.datetime.IsDateTime

/**
  * Created by alonsodomin on 11/12/2016.
  */
package object javatime {

  implicit lazy val javaLocalDateEq: Eq[LocalDate] =
    Eq.fromUniversalEquals[LocalDate]
  implicit lazy val javaLocalTimeEq: Eq[LocalTime] =
    Eq.fromUniversalEquals[LocalTime]
  implicit lazy val javaLocalDateTimeEq: Eq[LocalDateTime] =
    Eq.fromUniversalEquals[LocalDateTime]
  implicit lazy val javaZonedDateTimeEq: Eq[ZonedDateTime] =
    Eq.fromUniversalEquals[ZonedDateTime]
  implicit lazy val javaOffsetDateTimeEq: Eq[OffsetDateTime] =
    Eq.fromUniversalEquals[OffsetDateTime]

  implicit lazy val javaLocalDateShow: Show[LocalDate] = Show.fromToString
  implicit lazy val javaLocalTimeShow: Show[LocalTime] = Show.fromToString
  implicit lazy val javaLocalDateTimeShow: Show[LocalDateTime] =
    Show.fromToString
  implicit lazy val javaZonedDateTimeShow: Show[ZonedDateTime] =
    Show.fromToString
  implicit lazy val javaOffsetDateTimeShow: Show[OffsetDateTime] =
    Show.fromToString

  implicit def javaTemporalInstance[DT <: Temporal]: IsDateTime[DT] =
    new JavaTemporalInstance[DT]

}
