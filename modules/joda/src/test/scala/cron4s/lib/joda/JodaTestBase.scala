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

package cron4s.lib.joda

import cron4s.testkit.DateTimeTestKitBase

import org.joda.time._

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait JodaDateTimeTestBase extends DateTimeTestKitBase[DateTime] {

  protected def createDateTime(
      seconds: Int,
      minutes: Int,
      hours: Int,
      dayOfMonth: Int,
      month: Int,
      year: Int
  ): DateTime =
    new DateTime(year, month, dayOfMonth, hours, minutes, seconds, DateTimeZone.UTC)

}

trait JodaLocalDateTestBase extends DateTimeTestKitBase[LocalDate] {

  protected def createDateTime(
      seconds: Int,
      minutes: Int,
      hours: Int,
      dayOfMonth: Int,
      month: Int,
      year: Int
  ): LocalDate =
    new LocalDate(year, month, dayOfMonth)

}

trait JodaLocalTimeTestBase extends DateTimeTestKitBase[LocalTime] {

  protected def createDateTime(
      seconds: Int,
      minutes: Int,
      hours: Int,
      dayOfMonth: Int,
      month: Int,
      year: Int
  ): LocalTime =
    new LocalTime(hours, minutes, seconds)

}

trait JodaLocalDateTimeTestBase extends DateTimeTestKitBase[LocalDateTime] {

  protected def createDateTime(
      seconds: Int,
      minutes: Int,
      hours: Int,
      dayOfMonth: Int,
      month: Int,
      year: Int
  ): LocalDateTime =
    new LocalDateTime(year, month, dayOfMonth, hours, minutes, seconds)

}
