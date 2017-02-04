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

import cron4s.testkit.IsDateTimeTestKit
import org.threeten.bp.{LocalDate, LocalDateTime, LocalTime, ZonedDateTime}

/**
  * Created by alonsodomin on 29/08/2016.
  */
class ThreeTenBPLocalDateSpec extends IsDateTimeTestKit[LocalDate]("ThreeTenBPLocalDate")
  with ThreeTenBPLocalDateTestBase

class ThreeTenBPLocalTimeSpec extends IsDateTimeTestKit[LocalTime]("ThreeTenBPLocalTime")
  with ThreeTenBPLocalTimeTestBase

class ThreeTenBPLocalDateTimeSpec extends IsDateTimeTestKit[LocalDateTime]("ThreeTenBPLocalDateTime")
  with ThreeTenBPLocalDateTimeTestBase

class ThreeTenBPZonedDateTimeSpec extends IsDateTimeTestKit[ZonedDateTime]("ThreeTenBPZonedDateTime")
  with ThreeTenBPZonedDateTimeTestBase
