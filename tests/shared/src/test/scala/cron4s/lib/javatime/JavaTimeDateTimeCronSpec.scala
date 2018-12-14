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

import cron4s.testkit.DateTimeCronTestKit

/**
  * Created by alonsodomin on 29/01/2017.
  */
class JavaLocalDateCronSpec
    extends DateTimeCronTestKit[LocalDate]
    with JavaLocalDateTestBase
class JavaLocalTimeCronSpec
    extends DateTimeCronTestKit[LocalTime]
    with JavaLocalTimeTestBase
class JavaLocalDateTimeCronSpec
    extends DateTimeCronTestKit[LocalDateTime]
    with JavaLocalDateTimeTestBase
class JavaZonedDateTimeCronSpec
    extends DateTimeCronTestKit[ZonedDateTime]
    with JavaZonedDateTimeTestBase
class JavaOffsetDateTimeCronSpec
    extends DateTimeCronTestKit[OffsetDateTime]
    with JavaOffsetDateTimeTestBase
