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

import cron4s.CronField
import cron4s.CronField._
import cron4s.datetime.IsDateTime

import org.threeten.bp.temporal.{ChronoField, Temporal, TemporalField}

import scala.util.Try

/**
  * Created by alonsodomin on 30/01/2017.
  */
private[threetenbp] final class JSR310Instance[DT <: Temporal] extends IsDateTime[DT] {

  private[this] def mapField(field: CronField): TemporalField = field match {
    case Second     => ChronoField.SECOND_OF_MINUTE
    case Minute     => ChronoField.MINUTE_OF_HOUR
    case Hour       => ChronoField.HOUR_OF_DAY
    case DayOfMonth => ChronoField.DAY_OF_MONTH
    case Month      => ChronoField.MONTH_OF_YEAR
    case DayOfWeek  => ChronoField.DAY_OF_WEEK
  }

  override def supportedFields(dateTime: DT): List[CronField] =
    CronField.All.filter(f => dateTime.isSupported(mapField(f)))

  override def get[F <: CronField](dateTime: DT, field: F): Option[Int] = {
    val temporalField = mapField(field)

    val offset = if (field == DayOfWeek) -1 else 0
    if (!dateTime.isSupported(temporalField)) None
    else Some(dateTime.get(temporalField) + offset)
  }

  override def set[F <: CronField](dateTime: DT, field: F, value: Int): Option[DT] = {
    val temporalField = mapField(field)

    val offset = if (field == DayOfWeek) 1 else 0
    if (!dateTime.isSupported(temporalField)) None
    else Try(dateTime.`with`(temporalField, value.toLong + offset).asInstanceOf[DT]).toOption
  }

}
