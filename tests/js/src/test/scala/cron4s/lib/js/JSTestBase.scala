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

package cron4s.lib.js

import cron4s.testkit.DateTimeTestKitBase

import scala.scalajs.js.Date

/**
  * Created by alonsodomin on 02/09/2016.
  */
trait JSTestBase extends DateTimeTestKitBase[Date] {

  protected def createDateTime(seconds: Int,
                               minutes: Int,
                               hours: Int,
                               dayOfMonth: Int,
                               month: Int,
                               year: Int): Date =
    new Date(
      Date.UTC(year, month - 1, dayOfMonth, hours, minutes, seconds, ms = 0))

}
