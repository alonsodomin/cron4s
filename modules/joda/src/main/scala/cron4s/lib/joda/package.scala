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

import cats.{Eq, Show}

import cron4s.datetime.IsDateTime

import org.joda.time.{DateTime, LocalDate, LocalDateTime, LocalTime}

/** Created by alonsodomin on 11/12/2016.
  */
package object joda {
  implicit val jodaDateTimeEq: Eq[DateTime] = Eq.fromUniversalEquals[DateTime]
  implicit val jodaLocalDateEq: Eq[LocalDate] =
    Eq.fromUniversalEquals[LocalDate]
  implicit val jodaLocalTimeEq: Eq[LocalTime] =
    Eq.fromUniversalEquals[LocalTime]
  implicit val jodaLocalDateTimeEq: Eq[LocalDateTime] =
    Eq.fromUniversalEquals[LocalDateTime]

  implicit val jodaDateTimeShow: Show[DateTime]           = Show.fromToString
  implicit val jodaLocalDateShow: Show[LocalDate]         = Show.fromToString
  implicit val jodaLocalTimeShow: Show[LocalTime]         = Show.fromToString
  implicit val jodaLocalDateTimeShow: Show[LocalDateTime] = Show.fromToString

  implicit val jodaDateTimeInstance: IsDateTime[DateTime] =
    new JodaDateTimeInstance
  implicit val jodaLocalDateInstance: IsDateTime[LocalDate] =
    new JodaLocalDateInstance
  implicit val jodaLocalTimeInstance: IsDateTime[LocalTime] =
    new JodaLocalTimeInstance
  implicit val jodaLocalDateTimeInstance: IsDateTime[LocalDateTime] =
    new JodaLocalDateTimeInstance
}
