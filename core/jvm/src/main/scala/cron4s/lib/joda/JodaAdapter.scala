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

import cron4s.CronField
import cron4s.CronField._
import cron4s.datetime.DateTimeAdapter
import org.joda.time.{DateTime, DateTimeFieldType}

import scala.util.Try

/**
  * Created by alonsodomin on 30/01/2017.
  */
private[joda] final class JodaAdapter extends DateTimeAdapter[DateTime] {

  private[this] def mapField[F <: CronField](field: F): DateTimeFieldType = field match {
    case Second     => DateTimeFieldType.secondOfMinute()
    case Minute     => DateTimeFieldType.minuteOfHour()
    case Hour       => DateTimeFieldType.hourOfDay()
    case DayOfMonth => DateTimeFieldType.dayOfMonth()
    case Month      => DateTimeFieldType.monthOfYear()
    case DayOfWeek  => DateTimeFieldType.dayOfWeek()
  }

  override def supportedFields(dateTime: DateTime): List[CronField] =
    CronField.All.filter(f => dateTime.isSupported(mapField(f)))

  override def get[F <: CronField](dateTime: DateTime, field: F): Option[Int] = {
    val jodaField = mapField(field)
    val offset = if (field == DayOfWeek) -1 else 0

    if (!dateTime.isSupported(jodaField)) None
    else Some(dateTime.get(jodaField) + offset)
  }

  override def set[F <: CronField](dateTime: DateTime, field: F, value: Int): Option[DateTime] = {
    val jodaField = mapField(field)
    val offset = if (field == DayOfWeek) 1 else 0

    if (!dateTime.isSupported(jodaField)) None
    else Try(dateTime.withField(jodaField, value + offset)).toOption
  }

}
