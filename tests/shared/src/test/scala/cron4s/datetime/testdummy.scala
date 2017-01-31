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

package cron4s.datetime

import cron4s._

import scalaz.Equal

/**
  * Created by alonsodomin on 04/08/2016.
  */
object testdummy {
  import CronField._

  implicit val dummyDateTimeEq: Equal[DummyDateTime] = Equal.equalA[DummyDateTime]

  implicit object TestDummyAdapter extends DateTimeAdapter[DummyDateTime] {

    override def supportedFields(dateTime: DummyDateTime): List[CronField] = CronField.All

    override def get[F <: CronField](dateTime: DummyDateTime, field: F): Option[Int] = Some(field match {
      case Second     => dateTime.seconds
      case Minute     => dateTime.minutes
      case Hour       => dateTime.hours
      case DayOfMonth => dateTime.dayOfMonth
      case Month      => dateTime.month
      case DayOfWeek  => dateTime.dayOfWeek
    })

    override def set[F <: CronField](dateTime: DummyDateTime, field: F, value: Int): Option[DummyDateTime] = {
      Some(field match {
        case Second     => dateTime.copy(seconds = value)
        case Minute     => dateTime.copy(minutes = value)
        case Hour       => dateTime.copy(hours = value)
        case DayOfMonth => dateTime.copy(dayOfMonth = value)
        case Month      => dateTime.copy(month = value)
        case DayOfWeek  => dateTime.copy(dayOfWeek = value)
      })
    }
  }

}
