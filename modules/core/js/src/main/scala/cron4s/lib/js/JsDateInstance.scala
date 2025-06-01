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

package cron4s.lib.js

import cron4s.CronField
import cron4s.datetime.{DateTimeError, DateTimeUnit, InvalidFieldValue, IsDateTime}

import scala.scalajs.js.Date

/**
  * Created by alonsodomin on 30/01/2017.
  */
private[js] final class JsDateInstance extends IsDateTime[Date] {
  import CronField._
  import DateTimeUnit._

  val DaysInWeek = 7

  @inline
  override def supportedFields(dateTime: Date): List[CronField] = CronField.All

  override def plus(
      dateTime: Date,
      amount: Int,
      unit: DateTimeUnit
  ): Option[Date] = {
    def setter(set: Date => Unit): Date = {
      val newDateTime = new Date(dateTime.getTime())
      set(newDateTime)
      newDateTime
    }

    unit match {
      case Seconds =>
        Some(setter(d => d.setUTCSeconds(d.getUTCSeconds() + amount)))
      case Minutes =>
        Some(setter(d => d.setUTCMinutes(d.getUTCMinutes() + amount)))
      case Hours  => Some(setter(d => d.setUTCHours(d.getUTCHours() + amount)))
      case Days   => Some(setter(d => d.setUTCDate(d.getUTCDate() + amount)))
      case Months => Some(setter(d => d.setUTCMonth(d.getUTCMonth() + amount)))
      case Weeks  =>
        Some(setter(d => d.setUTCDate(d.getUTCDate() + (amount * DaysInWeek))))
    }
  }

  override def get[F <: CronField](dateTime: Date, field: F): Either[DateTimeError, Int] = {
    val value = field match {
      case Second     => dateTime.getUTCSeconds()
      case Minute     => dateTime.getUTCMinutes()
      case Hour       => dateTime.getUTCHours()
      case DayOfMonth => dateTime.getUTCDate()
      case Month      => dateTime.getUTCMonth() + 1
      case DayOfWeek  =>
        val dayOfWeek = {
          val idx = dateTime.getUTCDay() - 1
          if (idx < 0) DaysInWeek + idx
          else idx
        }
        dayOfWeek
    }

    Right(value.toInt)
  }

  override def set[F <: CronField](
      dateTime: Date,
      field: F,
      value: Int
  ): Either[DateTimeError, Date] = {
    def setter(setter: Date => Unit): Date = {
      val newDateTime = new Date(dateTime.getTime())
      setter(newDateTime)
      newDateTime
    }

    def assignFieldValue: Date =
      field match {
        case Second     => setter(_.setUTCSeconds(value, 0))
        case Minute     => setter(_.setUTCMinutes(value))
        case Hour       => setter(_.setUTCHours(value))
        case DayOfMonth => setter(_.setUTCDate(value))
        case Month      =>
          val monthToSet = value - 1
          setter(_.setUTCMonth(monthToSet))
        case DayOfWeek =>
          val dayToSet = (value % DaysInWeek) + 1
          val offset   = dayToSet - dateTime.getUTCDay()
          setter(d => d.setUTCDate(d.getUTCDate() + offset))
      }

    def assignmentSucceeded(date: Date) =
      get[F](date, field).toOption.contains(value)

    val modifiedDate = assignFieldValue
    if (assignmentSucceeded(modifiedDate)) Right(modifiedDate)
    else Left(InvalidFieldValue(field, value))
  }
}
