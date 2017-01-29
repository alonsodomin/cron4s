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

import java.time.{LocalDateTime, ZonedDateTime}

import cron4s.testkit.DateTimeAdapterTestKit

/**
  * Created by alonsodomin on 29/08/2016.
  */
class LocalDateTimeAdapterSpec extends DateTimeAdapterTestKit[LocalDateTime]("LocalDateTime") with LocalDateTimeTestBase
class ZonedDateTimeAdapterSpec extends DateTimeAdapterTestKit[ZonedDateTime]("ZonedDateTime") with ZonedDateTimeTestBase
