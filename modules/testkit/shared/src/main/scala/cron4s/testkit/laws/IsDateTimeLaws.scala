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

import cats.Eq
import cats.kernel.laws._
import cats.implicits._

import cron4s.CronField
import cron4s.datetime.IsDateTime
import cron4s.testkit._

import org.scalacheck.Prop
import Prop._

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait IsDateTimeLaws[DateTime] {
  implicit def DT: IsDateTime[DateTime]
  implicit def eq: Eq[DateTime]

  def gettable[F <: CronField](dt: DateTime, field: F): IsEq[Boolean] =
    DT.get(dt, field).isRight <-> DT.supportedFields(dt).contains(field)

  def immutability[F <: CronField](dt: DateTime, fieldValue: CronFieldValue[F]): Prop =
    if (DT.supportedFields(dt).contains(fieldValue.field)) {
      val check = for {
        current     <- DT.get(dt, fieldValue.field)
        newDateTime <- DT.set(dt, fieldValue.field, fieldValue.value)
      } yield {
        if (current === fieldValue.value) Prop.undecided
        else Prop(newDateTime =!= dt)
      }

      check.fold(Prop.exception(_), identity)
    } else Prop.proved

  def settable[F <: CronField](dt: DateTime, fieldValue: CronFieldValue[F]): Prop =
    if (DT.supportedFields(dt).contains(fieldValue.field)) {
      val check = for {
        newDateTime <- DT.set(dt, fieldValue.field, fieldValue.value)
        value       <- DT.get(newDateTime, fieldValue.field)
      } yield value

      check.fold(Prop.exception(_), _ ?= fieldValue.value)
    } else Prop.proved

}

object IsDateTimeLaws {

  def apply[DateTime](
      implicit
      dtEv: IsDateTime[DateTime],
      eqEv: Eq[DateTime]
  ): IsDateTimeLaws[DateTime] =
    new IsDateTimeLaws[DateTime] {
      implicit val eq: Eq[DateTime]         = eqEv
      implicit val DT: IsDateTime[DateTime] = dtEv
    }

}
