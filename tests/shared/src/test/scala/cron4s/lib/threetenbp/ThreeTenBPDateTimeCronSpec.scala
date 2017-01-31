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

import cron4s.testkit.DateTimeCronTestKit
import org.threeten.bp.{LocalDate, LocalDateTime, LocalTime, ZonedDateTime}

/**
  * Created by alonsodomin on 29/01/2017.
  */
class ThreeTenBPLocalDateCronSpec extends DateTimeCronTestKit[LocalDate] with ThreeTenBPLocalDateTestBase
class ThreeTenBPLocalTimeCronSpec extends DateTimeCronTestKit[LocalTime] with ThreeTenBPLocalTimeTestBase
class ThreeTenBPLocalDateTimeCronSpec extends DateTimeCronTestKit[LocalDateTime] with ThreeTenBPLocalDateTimeTestBase
class ThreeTenBPZonedDateTimeCronSpec extends DateTimeCronTestKit[ZonedDateTime] with ThreeTenBPZonedDateTimeTestBase
