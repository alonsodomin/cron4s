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

package cron4s
package lib.joda

import cats.syntax.either._

import cron4s._
import cron4s.datetime._

import org.joda.time._
import org.joda.time.base.BaseLocal

import scala.util.Try

/**
  * Created by alonsodomin on 06/02/2017.
  */
private[joda] abstract class JodaInstance[DT] extends IsDateTime[DT] {
  import CronField._

  override def plus(dateTime: DT, amount: Int, unit: DateTimeUnit): Option[DT] =
    plusPeriod(dateTime, asPeriod(amount, unit))

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
  override def get[F <: CronField](dateTime: DT, field: F): Either[DateTimeStepError, Int] = {
    val jodaField = asDateTimeFieldType(field)
    if (isSupported(dateTime, jodaField)) {
      val offset = if (field == DayOfWeek) -1 else 0

      getField(dateTime, jodaField)
        .map(_ + offset)
        .fold(UnsupportedField(field).asLeft[Int])(_.asRight)
    } else {
      UnsupportedField(field).asLeft
    }
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
  override def set[F <: CronField](
      dateTime: DT,
      field: F,
      value: Int
  ): Either[DateTimeStepError, DT] = {
    val offset = if (field == DayOfWeek) 1 else 0
    setField(dateTime, field, value + offset)
  }

  protected def asPeriod(amount: Int, unit: DateTimeUnit): ReadablePeriod =
    unit match {
      case DateTimeUnit.Seconds => Seconds.seconds(amount)
      case DateTimeUnit.Minutes => Minutes.minutes(amount)
      case DateTimeUnit.Hours   => Hours.hours(amount)
      case DateTimeUnit.Days    => Days.days(amount)
      case DateTimeUnit.Months  => Months.months(amount)
      case DateTimeUnit.Weeks   => Weeks.weeks(amount)
    }

  protected def asDateTimeFieldType[F <: CronField](field: F): DateTimeFieldType = field match {
    case Second     => DateTimeFieldType.secondOfMinute()
    case Minute     => DateTimeFieldType.minuteOfHour()
    case Hour       => DateTimeFieldType.hourOfDay()
    case DayOfMonth => DateTimeFieldType.dayOfMonth()
    case Month      => DateTimeFieldType.monthOfYear()
    case DayOfWeek  => DateTimeFieldType.dayOfWeek()
  }

  protected def isSupported(dateTime: DT, field: DateTimeFieldType): Boolean

  protected def plusPeriod(dateTime: DT, period: ReadablePeriod): Option[DT]

  protected def getField(dateTime: DT, field: DateTimeFieldType): Option[Int]

  protected def setField(dateTime: DT, field: CronField, value: Int): Either[DateTimeStepError, DT]

}

private[joda] final class JodaDateTimeInstance extends JodaInstance[DateTime] {

  override protected def isSupported(dateTime: DateTime, field: DateTimeFieldType): Boolean =
    dateTime.isSupported(field)

  override protected def plusPeriod(dateTime: DateTime, period: ReadablePeriod): Option[DateTime] =
    Try(dateTime.plus(period)).toOption

  override protected def getField(dateTime: DateTime, field: DateTimeFieldType): Option[Int] =
    if (dateTime.isSupported(field)) Some(dateTime.get(field))
    else None

  override protected def setField(
      dateTime: DateTime,
      field: CronField,
      value: Int
  ): Either[DateTimeStepError, DateTime] = {
    val fieldType = asDateTimeFieldType(field)

    if (dateTime.isSupported(fieldType)) {
      val newDate = Either
        .catchNonFatal(dateTime.withField(fieldType, value))
        .leftMap(_ => InvalidFieldValue(field, value))

      if (fieldType.equals(DateTimeFieldType.secondOfMinute()))
        newDate.map(_.withMillisOfSecond(0))
      else newDate
    } else UnsupportedField(field).asLeft
  }

}

private[joda] abstract class JodaLocalBaseInstance[DT <: BaseLocal] extends JodaInstance[DT] {

  override protected def isSupported(dateTime: DT, field: DateTimeFieldType): Boolean =
    dateTime.isSupported(field)

  override protected def getField(dateTime: DT, field: DateTimeFieldType): Option[Int] =
    if (dateTime.isSupported(field)) Some(dateTime.get(field))
    else None

}

private[joda] final class JodaLocalTimeInstance extends JodaLocalBaseInstance[LocalTime] {

  override protected def plusPeriod(
      dateTime: LocalTime,
      period: ReadablePeriod
  ): Option[LocalTime] =
    Some(dateTime.plus(period))

  override protected def setField(
      dateTime: LocalTime,
      field: CronField,
      value: Int
  ): Either[DateTimeStepError, LocalTime] = {
    val fieldType = asDateTimeFieldType(field)

    if (dateTime.isSupported(fieldType)) {
      val newDate = Either
        .fromTry(Try(dateTime.withField(fieldType, value)))
        .leftMap(_ => InvalidFieldValue(field, value))

      if (fieldType.equals(DateTimeFieldType.secondOfMinute()))
        newDate.map(_.withMillisOfSecond(0))
      else newDate
    } else UnsupportedField(field).asLeft
  }

}

private[joda] final class JodaLocalDateInstance extends JodaLocalBaseInstance[LocalDate] {

  override protected def plusPeriod(
      dateTime: LocalDate,
      period: ReadablePeriod
  ): Option[LocalDate] =
    Try(dateTime.plus(period)).toOption

  override protected def setField(
      dateTime: LocalDate,
      field: CronField,
      value: Int
  ): Either[DateTimeStepError, LocalDate] = {
    val fieldType = asDateTimeFieldType(field)

    if (dateTime.isSupported(fieldType)) {
      Either
        .catchNonFatal(dateTime.withField(fieldType, value))
        .leftMap(_ => InvalidFieldValue(field, value))
    } else UnsupportedField(field).asLeft
  }

}

private[joda] final class JodaLocalDateTimeInstance extends JodaLocalBaseInstance[LocalDateTime] {

  override protected def plusPeriod(
      dateTime: LocalDateTime,
      period: ReadablePeriod
  ): Option[LocalDateTime] =
    Try(dateTime.plus(period)).toOption

  override protected def setField(
      dateTime: LocalDateTime,
      field: CronField,
      value: Int
  ): Either[DateTimeStepError, LocalDateTime] = {
    val fieldType = asDateTimeFieldType(field)

    if (dateTime.isSupported(fieldType)) {
      val newDate = Either
        .catchNonFatal(dateTime.withField(fieldType, value))
        .leftMap(_ => InvalidFieldValue(field, value))

      if (fieldType.equals(DateTimeFieldType.secondOfMinute()))
        newDate.map(_.withMillisOfSecond(0))
      else newDate
    } else UnsupportedField(field).asLeft
  }

}
