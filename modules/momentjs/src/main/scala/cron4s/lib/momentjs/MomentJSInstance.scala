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
package lib.momentjs

import cats.syntax.either._

import cron4s.CronField
import cron4s.datetime.{DateTimeUnit, IsDateTime}

import moment._

/**
  * Created by alonsodomin on 11/04/2017.
  */
private[momentjs] final class MomentJSInstance extends IsDateTime[Date] {
  import CronField._
  import DateTimeUnit._

  val DaysInWeek = 7

  override def plus(dateTime: Date, amount: Int, unit: DateTimeUnit): Option[Date] =
    Some(unit match {
      case Seconds => dateTime.add(amount, Units.Second)
      case Minutes => dateTime.add(amount, Units.Minute)
      case Hours   => dateTime.add(amount, Units.Hour)
      case Days    => dateTime.add(amount, Units.Day)
      case Weeks   => dateTime.add(amount, Units.Week)
      case Months  => dateTime.add(amount, Units.Month)
    })

  /**
    * List of the fields supported by this date time representation
    *
    * @param dateTime the date time representation
    * @return list of the supported fields
    */
  @inline
  override def supportedFields(dateTime: Date): List[CronField] = CronField.All

  /**
    * Getter access for a specific field in a date-time
    *
    * @param dateTime a date-time
    * @param field    a CronField
    * @tparam F the CronField type
    * @return value of the field
    */
  override def get[F <: CronField](dateTime: Date, field: F): Either[DateTimeStepError, Int] =
    Right(field match {
      case Second     => dateTime.second()
      case Minute     => dateTime.minute()
      case Hour       => dateTime.hour()
      case DayOfMonth => dateTime.date()
      case Month      => dateTime.month() + 1
      case DayOfWeek =>
        val dayOfWeek = {
          val idx = dateTime.day() - 1
          if (idx < 0) DaysInWeek + idx
          else idx
        }
        dayOfWeek
    })

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
      dateTime: Date,
      field: F,
      value: Int
  ): Either[DateTimeStepError, Date] = {
    def setter(f: Date => Unit): Date = {
      val newDateTime = Moment(dateTime)
      f(newDateTime)
      newDateTime
    }

    def assignFieldValue: Date = field match {
      case Second     => setter(_.second(value.toDouble).millisecond(0))
      case Minute     => setter(_.minute(value.toDouble))
      case Hour       => setter(_.hour(value.toDouble))
      case DayOfMonth => setter(_.date(value.toDouble))
      case Month      => setter(_.month((value - 1).toDouble))
      case DayOfWeek =>
        val dayToSet = (value % DaysInWeek) + 1
        setter(_.day(dayToSet.toDouble))
    }

    def assignmentSucceeded(date: Date) =
      get[F](date, field).toOption.contains(value)

    val modifiedDate = assignFieldValue
    if (assignmentSucceeded(modifiedDate)) Right(modifiedDate)
    else Left(InvalidFieldValue(field, value))
  }

}
