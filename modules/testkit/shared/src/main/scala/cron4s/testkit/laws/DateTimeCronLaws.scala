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

import cats.laws._
import cats.syntax.either._

import cron4s.CronField
import cron4s.datetime.{DateTimeCron, IsDateTime}
import cron4s.syntax.cron._

/**
  * Created by alonsodomin on 29/01/2017.
  */
trait DateTimeCronLaws[E, DateTime] {
  implicit def DT: IsDateTime[DateTime]
  implicit def TC: DateTimeCron[E]

  def matchAny(e: E, dt: DateTime): IsEq[Boolean] = {
    val fieldValues =
      e.supportedFields.flatMap(DT.get(dt, _).toOption)

    val exprRanges = e.ranges
    val supportedRanges =
      DT.supportedFields(dt).flatMap(exprRanges.get)

    val existsAny = supportedRanges
      .zip(fieldValues)
      .exists { case (range, value) => range.contains(value) }

    e.anyOf(dt) <-> existsAny
  }

  def matchAll(e: E, dt: DateTime): IsEq[Boolean] = {
    val fieldValues =
      e.supportedFields.flatMap(DT.get(dt, _).toOption)

    val exprRanges = e.ranges
    val supportedRanges =
      DT.supportedFields(dt).flatMap(exprRanges.get)

    val containsAll = supportedRanges
      .zip(fieldValues)
      .forall { case (range, value) => range.contains(value) }

    e.allOf(dt) <-> containsAll
  }

  def forwards(e: E, from: DateTime): IsEq[Option[DateTime]] =
    e.next(from) <-> e.step(from, 1)

  def backwards(e: E, from: DateTime): IsEq[Option[DateTime]] =
    e.prev(from) <-> e.step(from, -1)

  def supportedFieldsEquality(e: E): IsEq[List[CronField]] =
    supportedFields[E] <-> e.supportedFields
}

object DateTimeCronLaws {
  def apply[E, DateTime](implicit
      dt0: IsDateTime[DateTime],
      TC0: DateTimeCron[E]
  ): DateTimeCronLaws[E, DateTime] =
    new DateTimeCronLaws[E, DateTime] {
      implicit val DT = dt0
      implicit val TC = TC0
    }
}
