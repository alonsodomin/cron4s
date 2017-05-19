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

import java.time.temporal._

import cron4s.CronField
import cron4s.datetime.{DateTimeUnit, IsDateTime}

import scala.util.{Success, Try}

/**
  * Created by alonsodomin on 30/01/2017.
  */
private[javatime] final class JavaTemporalInstance[DT <: Temporal] extends IsDateTime[DT] {
  import CronField._
  import DateTimeUnit._

  private[this] def asTemporalField(field: CronField): TemporalField = field match {
    case Second     => ChronoField.SECOND_OF_MINUTE
    case Minute     => ChronoField.MINUTE_OF_HOUR
    case Hour       => ChronoField.HOUR_OF_DAY
    case DayOfMonth => ChronoField.DAY_OF_MONTH
    case Month      => ChronoField.MONTH_OF_YEAR
    case DayOfWeek  => ChronoField.DAY_OF_WEEK
  }

  private[this] def asTemporalUnit[F <: CronField](unit: DateTimeUnit): TemporalUnit = unit match {
    case Seconds => ChronoUnit.SECONDS
    case Minutes => ChronoUnit.MINUTES
    case Hours   => ChronoUnit.HOURS
    case Days    => ChronoUnit.DAYS
    case Months  => ChronoUnit.MONTHS
    case Weeks   => ChronoUnit.WEEKS
  }

  override def plus(dateTime: DT, amount: Int, unit: DateTimeUnit): Option[DT] =
    Try(dateTime.plus(amount.toLong, asTemporalUnit(unit)).asInstanceOf[DT]).toOption

  def supportedFields(dateTime: DT): List[CronField] =
    CronField.All.filter(f => dateTime.isSupported(asTemporalField(f)))

  override def get[F <: CronField](dateTime: DT, field: F): Option[Int] = {
    val temporalField = asTemporalField(field)

    val offset = if (field == DayOfWeek) -DayOfWeekOffset else 0
    if (!dateTime.isSupported(temporalField)) None
    else Some(dateTime.get(temporalField) + offset)
  }

  override def set[F <: CronField](dateTime: DT, field: F, value: Int): Option[DT] = {
    val temporalField = asTemporalField(field)

    def adjust(realVal: Int)(dt: DT): Try[DT] =
      Try(dt.`with`(temporalField, realVal.toLong).asInstanceOf[DT])

    def postAdjust(dt: DT): Try[DT] = {
      if (dt.isSupported(ChronoField.MILLI_OF_SECOND)) {
        Try(dt.`with`(ChronoField.MILLI_OF_SECOND, 0).asInstanceOf[DT])
      } else Success(dt)
    }

    if (!dateTime.isSupported(temporalField)) None
    else {
      val realVal = if (field == DayOfWeek) value + DayOfWeekOffset else value
      adjust(realVal)(dateTime).flatMap(postAdjust).toOption
    }
  }

}
