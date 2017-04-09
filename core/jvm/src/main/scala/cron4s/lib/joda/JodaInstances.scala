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

import cron4s.{CronField, CronUnit}
import cron4s.datetime.IsDateTime

import org.joda.time._
import org.joda.time.base.BaseLocal

import scala.util.Try

/**
  * Created by alonsodomin on 06/02/2017.
  */
private[joda] abstract class JodaInstance[DT] extends IsDateTime[DT] {
  import CronField._

  /**
    * List of the fields supported by this date time representation
    *
    * @param dateTime the date time representation
    * @return list of the supported fields
    */
  override def supportedFields(dateTime: DT): List[CronField] =
    CronField.All.filter(field => isSupported(dateTime, asDateTimeFieldType(field)))

  /**
    * Getter access for a specific field in a date-time
    *
    * @param dateTime a date-time
    * @param field    a CronField
    * @tparam F the CronField type
    * @return value of the field
    */
  override def get[F <: CronField](dateTime: DT, field: F): Option[Int] = {
    val jodaField = asDateTimeFieldType(field)
    val offset = if (field == DayOfWeek) -1 else 0

    getField(dateTime, jodaField).map(_ + offset)
  }

  /**
    * Setter access for a specific field in a date-time
    *
    * @param dateTime a date-time
    * @param field    a CronField
    * @param value    new value for the field
    * @tparam F the CronField type
    * @return a new date-time with the given field set to the new value
    */
  override def set[F <: CronField](dateTime: DT, field: F, value: Int): Option[DT] = {
    val jodaField = asDateTimeFieldType(field)
    val offset = if (field == DayOfWeek) 1 else 0

    setField(dateTime, jodaField, value + offset)
  }

  protected def asPeriod[F <: CronField](amount: Int, unit: CronUnit[F]): Option[ReadablePeriod] =
    unit match {
      case CronUnit.Seconds     => Some(Seconds.seconds(amount))
      case CronUnit.Minutes     => Some(Minutes.minutes(amount))
      case CronUnit.Hours       => Some(Hours.hours(amount))
      case CronUnit.DaysOfMonth => Some(Days.days(amount))
      case CronUnit.Months      => Some(Months.months(amount))
      case _                    => None
    }

  private[this] def asDateTimeFieldType[F <: CronField](field: F): DateTimeFieldType = field match {
    case Second     => DateTimeFieldType.secondOfMinute()
    case Minute     => DateTimeFieldType.minuteOfHour()
    case Hour       => DateTimeFieldType.hourOfDay()
    case DayOfMonth => DateTimeFieldType.dayOfMonth()
    case Month      => DateTimeFieldType.monthOfYear()
    case DayOfWeek  => DateTimeFieldType.dayOfWeek()
  }

  protected def isSupported(dateTime: DT, field: DateTimeFieldType): Boolean

  protected def getField(dateTime: DT, field: DateTimeFieldType): Option[Int]

  protected def setField(dateTime: DT, field: DateTimeFieldType, value: Int): Option[DT]

}

private[joda] final class JodaDateTimeInstance extends JodaInstance[DateTime] {

  override protected def isSupported(dateTime: DateTime, field: DateTimeFieldType): Boolean =
    dateTime.isSupported(field)

  override def plus[F <: CronField](dateTime: DateTime, amount: Int, unit: CronUnit[F]): Option[DateTime] =
    asPeriod(amount, unit).map(dateTime.plus)

  override protected def getField(dateTime: DateTime, field: DateTimeFieldType): Option[Int] = {
    if (dateTime.isSupported(field)) Some(dateTime.get(field))
    else None
  }

  override protected def setField(dateTime: DateTime, field: DateTimeFieldType, value: Int): Option[DateTime] = {
    if (dateTime.isSupported(field)) {
      val newDate = Try(dateTime.withField(field, value)).toOption
      if (field.equals(DateTimeFieldType.secondOfMinute()))
        newDate.map(_.withMillisOfSecond(0))
      else newDate
    } else None
  }

}

private[joda] abstract class JodaLocalBaseInstance[DT <: BaseLocal] extends JodaInstance[DT] {
  override protected def isSupported(dateTime: DT, field: DateTimeFieldType): Boolean =
    dateTime.isSupported(field)

  override protected def getField(dateTime: DT, field: DateTimeFieldType): Option[Int] = {
    if (dateTime.isSupported(field)) Some(dateTime.get(field))
    else None
  }

}

private[joda] final class JodaLocalTimeInstance extends JodaLocalBaseInstance[LocalTime] {

  override def plus[F <: CronField](dateTime: LocalTime, amount: Int, unit: CronUnit[F]): Option[LocalTime] =
    asPeriod(amount, unit).map(dateTime.plus)

  override protected def setField(dateTime: LocalTime, field: DateTimeFieldType, value: Int): Option[LocalTime] = {
    if (dateTime.isSupported(field)) {
      val newDate = Try(dateTime.withField(field, value)).toOption
      if (field.equals(DateTimeFieldType.secondOfMinute()))
        newDate.map(_.withMillisOfSecond(0))
      else newDate
    } else None
  }

}

private[joda] final class JodaLocalDateInstance extends JodaLocalBaseInstance[LocalDate] {

  override def plus[F <: CronField](dateTime: LocalDate, amount: Int, unit: CronUnit[F]): Option[LocalDate] =
    asPeriod(amount, unit).map(dateTime.plus)

  override protected def setField(dateTime: LocalDate, field: DateTimeFieldType, value: Int): Option[LocalDate] = {
    if (dateTime.isSupported(field)) {
      Try(dateTime.withField(field, value)).toOption
    } else None
  }

}

private[joda] final class JodaLocalDateTimeInstance extends JodaLocalBaseInstance[LocalDateTime] {

  override def plus[F <: CronField](dateTime: LocalDateTime, amount: Int, unit: CronUnit[F]): Option[LocalDateTime] =
    asPeriod(amount, unit).map(dateTime.plus)

  override protected def setField(dateTime: LocalDateTime, field: DateTimeFieldType, value: Int): Option[LocalDateTime] = {
    if (dateTime.isSupported(field)) {
      val newDate = Try(dateTime.withField(field, value)).toOption
      if (field.equals(DateTimeFieldType.secondOfMinute()))
        newDate.map(_.withMillisOfSecond(0))
      else newDate
    } else None
  }

}
