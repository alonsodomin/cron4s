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

package cron4s.expr

import cron4s.CronField

import shapeless.HList
import shapeless.ops.hlist.Selector

import scala.annotation.implicitNotFound

/**
  * Created by alonsodomin on 10/02/2017.
  */
@implicitNotFound("Field ${F} is not a member of expression ${A}")
trait FieldSelector[A, F <: CronField] {
  type Raw <: HList
  type Out[X <: CronField]

  final def selectFrom(expr: Raw)(implicit select: Selector[Raw, Out[F]]): Out[F] =
    select(expr)

}

object FieldSelector {
  import CronField._

  def apply[A <: HList, F <: CronField](implicit ev: FieldSelector[A, F]): FieldSelector[A, F] = ev

  implicit val SecondsFromCronExpr: FieldSelector[CronExpr, Second] = new FullCronFieldNodeSelector[Second]
  implicit val SecondsFromTimeExpr: FieldSelector[TimeCronExpr, Second] = new TimeCronFieldNodeSelector[Second]

  implicit val MinutesFromCronExpr: FieldSelector[CronExpr, Minute] = new FullCronFieldNodeSelector[Minute]
  implicit val MinutesFromTimeExpr: FieldSelector[TimeCronExpr, Minute] = new TimeCronFieldNodeSelector[Minute]

  implicit val HoursFromCronExpr: FieldSelector[CronExpr, Hour] = new FullCronFieldNodeSelector[Hour]
  implicit val HoursFromTimeExpr: FieldSelector[TimeCronExpr, Hour] = new TimeCronFieldNodeSelector[Hour]

  implicit val DayOfMonthFromCronExpr: FieldSelector[CronExpr, DayOfMonth] = new FullCronFieldNodeWithAnySelector[DayOfMonth]
  implicit val DayOfMonthFromDateExpr: FieldSelector[DateCronExpr, DayOfMonth] = new DateCronFieldNodeWithAnySelector[DayOfMonth]

  implicit val MonthFromCronExpr: FieldSelector[CronExpr, Month] = new FullCronFieldNodeWithAnySelector[Month]
  implicit val MonthFromDateExpr: FieldSelector[DateCronExpr, Month] = new DateCronFieldNodeSelector[Month]

  implicit val DayOfWeekFromCronExpr: FieldSelector[CronExpr, DayOfWeek] = new FullCronFieldNodeWithAnySelector[DayOfWeek]
  implicit val DayOfWeekFromDateExpr: FieldSelector[DateCronExpr, DayOfWeek] = new DateCronFieldNodeWithAnySelector[DayOfWeek]

  // Base classes adding type refinements for the typeclass instances

  private[this] abstract class FieldNodeSelector[A, F <: CronField] extends FieldSelector[A, F] {
    type Out[X <: CronField] = FieldNode[X]
  }
  private[this] class FullCronFieldNodeSelector[F <: CronField] extends FieldNodeSelector[CronExpr, F] {
    type Raw = RawCronExpr
  }
  private[this] class TimeCronFieldNodeSelector[F <: CronField] extends FieldNodeSelector[TimeCronExpr, F] {
    type Raw = RawTimeCronExpr
  }
  private[this] class DateCronFieldNodeSelector[F <: CronField] extends FieldNodeSelector[DateCronExpr, F] {
    type Raw = RawDateCronExpr
  }

  private[this] abstract class FieldNodeWithAnySelector[A, F <: CronField] extends FieldSelector[A, F] {
    type Out[X <: CronField] = FieldNodeWithAny[X]
  }
  private[this] class FullCronFieldNodeWithAnySelector[F <: CronField] extends FieldNodeWithAnySelector[CronExpr, F] {
    type Raw = RawCronExpr
  }
  private[this] class DateCronFieldNodeWithAnySelector[F <: CronField] extends FieldNodeWithAnySelector[DateCronExpr, F] {
    type Raw = RawDateCronExpr
  }

}