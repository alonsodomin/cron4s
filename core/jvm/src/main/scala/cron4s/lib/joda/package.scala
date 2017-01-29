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

package cron4s.lib

import cron4s.CronField
import cron4s.datetime.DateTimeAdapter

import org.joda.time.{DateTime, DateTimeFieldType}

import scalaz.Equal

/**
  * Created by alonsodomin on 11/12/2016.
  */
package object joda {
  import CronField._

  implicit val dateTimeInstance: Equal[DateTime] = Equal.equalA[DateTime]

  implicit object JodaTimeAdapter extends DateTimeAdapter[DateTime] {

    private[this] def mapField[F <: CronField](field: F): DateTimeFieldType = field match {
      case Second     => DateTimeFieldType.secondOfMinute()
      case Minute     => DateTimeFieldType.minuteOfHour()
      case Hour       => DateTimeFieldType.hourOfDay()
      case DayOfMonth => DateTimeFieldType.dayOfMonth()
      case Month      => DateTimeFieldType.monthOfYear()
      case DayOfWeek  => DateTimeFieldType.dayOfWeek()
    }

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
      else Some(dateTime.withField(jodaField, value + offset))
    }
  }

}
