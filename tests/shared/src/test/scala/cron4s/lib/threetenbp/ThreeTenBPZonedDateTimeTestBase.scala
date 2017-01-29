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

package cron4s.lib.threetenbp

import cron4s.testkit.DateTimeTestKitBase
import org.threeten.bp.{ZoneId, ZonedDateTime}

import scalaz.Equal

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait ThreeTenBPZonedDateTimeTestBase extends DateTimeTestKitBase[ZonedDateTime] {
  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): ZonedDateTime =
    ZonedDateTime.of(2016, month, dayOfMonth, hours, minutes, seconds, 0, ZoneId.of("UTC"))

  implicit val dateTimeEq: Equal[ZonedDateTime] = Equal.equal((lhs, rhs) => lhs.equals(rhs))
}
