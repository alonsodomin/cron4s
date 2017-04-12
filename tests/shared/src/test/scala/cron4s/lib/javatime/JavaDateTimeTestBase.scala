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

package cron4s.lib.javatime

import java.time._

import cron4s.testkit.DateTimeTestKitBase

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait JavaLocalDateTestBase extends DateTimeTestKitBase[LocalDate] {

  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, year: Int): LocalDate =
    LocalDate.of(year, month, dayOfMonth)
}

trait JavaLocalTimeTestBase extends DateTimeTestKitBase[LocalTime] {

  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, year: Int): LocalTime =
    LocalTime.of(hours, minutes, seconds)
}

trait JavaLocalDateTimeTestBase extends DateTimeTestKitBase[LocalDateTime] {

  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, year: Int): LocalDateTime =
    LocalDateTime.of(year, month, dayOfMonth, hours, minutes, seconds)

}

trait JavaZonedDateTimeTestBase extends DateTimeTestKitBase[ZonedDateTime] {

  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, year: Int): ZonedDateTime =
    ZonedDateTime.of(year, month, dayOfMonth, hours, minutes, seconds, 0, ZoneOffset.UTC)
}

trait JavaOffsetDateTimeTestBase extends DateTimeTestKitBase[OffsetDateTime] {

  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, year: Int): OffsetDateTime =
    OffsetDateTime.of(year, month, dayOfMonth, hours, minutes, seconds, 0, ZoneOffset.UTC)

}