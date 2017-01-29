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

import java.time.LocalDateTime

import cron4s.testkit.DateTimeTestKitBase

import scalaz.Equal

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait LocalDateTimeTestBase extends DateTimeTestKitBase[LocalDateTime] {
  implicit val dateTimeEq: Equal[LocalDateTime] = Equal.equal((lhs, rhs) => lhs.equals(rhs))

  def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): LocalDateTime =
    LocalDateTime.of(2016, month, dayOfMonth, hours, minutes, seconds)
}