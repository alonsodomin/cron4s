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

package cron4s.testkit.laws

import cron4s.CronField
import cron4s.datetime.DateTimeAdapter
import cron4s.testkit._

import org.scalacheck.Prop

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait DateTimeAdapterLaws[DateTime <: AnyRef] {
  implicit def adapter: DateTimeAdapter[DateTime]
  implicit val eq: Equal[DateTime]

  def immutability[F <: CronField](dt: DateTime, fieldValue: CronFieldValue[F]): Prop = {
    val check = for {
      current     <- adapter.get(dt, fieldValue.field)
      newDateTime <- adapter.set(dt, fieldValue.field, fieldValue.value)
    } yield {
      if (current == fieldValue.value) newDateTime ?== dt
      else newDateTime ?!= dt
    }

    check.getOrElse(proved)
  }

  def settable[F <: CronField](dt: DateTime, fieldValue: CronFieldValue[F]): Prop = {
    val check = for {
      newDateTime <- adapter.set(dt, fieldValue.field, fieldValue.value)
      value       <- adapter.get(newDateTime, fieldValue.field)
    } yield value ?== fieldValue.value

    check.getOrElse(proved)
  }

}

object DateTimeAdapterLaws {

  def apply[DateTime <: AnyRef](implicit
      adapterEv: DateTimeAdapter[DateTime],
      eqEv: Equal[DateTime]
  ): DateTimeAdapterLaws[DateTime] =
    new DateTimeAdapterLaws[DateTime] {
      implicit val eq: Equal[DateTime] = eqEv
      implicit val adapter: DateTimeAdapter[DateTime] = adapterEv
    }

}
