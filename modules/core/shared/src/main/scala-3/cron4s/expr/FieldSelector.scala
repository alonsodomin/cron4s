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


import scala.annotation.implicitNotFound

/**
  * Created by alonsodomin on 10/02/2017.
  */
@implicitNotFound("Field ${F} is not a member of expression ${A}")
sealed trait FieldSelector[A, F <: CronField] {
  type Raw <: Tuple
  type Out[X <: CronField]

  val hlistSelect: (expr:Raw) => Out[F]
  def selectFrom(expr: A): Out[F]
}

object FieldSelector {
  import CronField._

  def apply[A, F <: CronField](implicit ev: FieldSelector[A, F]): FieldSelector[A, F] = ev

  implicit val SecondsFromCronExpr: FieldSelector[CronExpr, Second] =
    new FullCronFieldNodeSelector[Second] {
      val hlistSelect  = (expr:RawCronExpr) => expr._1
    }
  implicit val SecondsFromTimeExpr: FieldSelector[TimeCronExpr, Second] =
    new TimeCronFieldNodeSelector[Second] {
      val hlistSelect  = (expr:RawTimeCronExpr) => expr._1
    }

  implicit val MinutesFromCronExpr: FieldSelector[CronExpr, Minute] =
    new FullCronFieldNodeSelector[Minute] {
      val hlistSelect  = (expr:RawCronExpr) => expr._2
    }
  implicit val MinutesFromTimeExpr: FieldSelector[TimeCronExpr, Minute] =
    new TimeCronFieldNodeSelector[Minute] {
      val hlistSelect  = (expr:RawTimeCronExpr) => expr._2

    }

  implicit val HoursFromCronExpr: FieldSelector[CronExpr, Hour] =
    new FullCronFieldNodeSelector[Hour] {
      val hlistSelect  = (expr:RawCronExpr) => expr._3
    }
  implicit val HoursFromTimeExpr: FieldSelector[TimeCronExpr, Hour] =
    new TimeCronFieldNodeSelector[Hour] {
      val hlistSelect  = (expr:RawTimeCronExpr) => expr._3
    }

  implicit val DayOfMonthFromCronExpr: FieldSelector[CronExpr, DayOfMonth] =
    new FullCronFieldNodeWithAnySelector[DayOfMonth] {
      val hlistSelect  = (expr:RawCronExpr) => expr._4
    }
  implicit val DayOfMonthFromDateExpr: FieldSelector[DateCronExpr, DayOfMonth] =
    new DateCronFieldNodeWithAnySelector[DayOfMonth] {
      val hlistSelect  = (expr:RawDateCronExpr) => expr._1
    }

  implicit val MonthFromCronExpr: FieldSelector[CronExpr, Month] =
    new FullCronFieldNodeSelector[Month] {
      val hlistSelect  = (expr:RawCronExpr) => expr._5
    }
  implicit val MonthFromDateExpr: FieldSelector[DateCronExpr, Month] =
    new DateCronFieldNodeSelector[Month] {
      val hlistSelect  = (expr:RawDateCronExpr) => expr._2
    }

  implicit val DayOfWeekFromCronExpr: FieldSelector[CronExpr, DayOfWeek] =
    new FullCronFieldNodeWithAnySelector[DayOfWeek] {
      val hlistSelect  = (expr:RawCronExpr) => expr._6
    }
  implicit val DayOfWeekFromDateExpr: FieldSelector[DateCronExpr, DayOfWeek] =
    new DateCronFieldNodeWithAnySelector[DayOfWeek] {
      val hlistSelect  = (expr:RawDateCronExpr) => expr._3
    }

  // Base classes adding type refinements for the typeclass instances

  private[this] abstract class FieldNodeSelector[A, F <: CronField] extends FieldSelector[A, F] {
    type Out[X <: CronField] = FieldNode[X]
  }
  private[this] abstract class FullCronFieldNodeSelector[F <: CronField]
      extends FieldNodeSelector[CronExpr, F] {
    type Raw = RawCronExpr

    def selectFrom(expr: CronExpr): FieldNode[F] = hlistSelect(expr.raw)
  }
  private[this] abstract class TimeCronFieldNodeSelector[F <: CronField]
      extends FieldNodeSelector[TimeCronExpr, F] {
    type Raw = RawTimeCronExpr

    def selectFrom(expr: TimeCronExpr): FieldNode[F] =  hlistSelect(expr.raw)
  }
  private[this] abstract class DateCronFieldNodeSelector[F <: CronField]
      extends FieldNodeSelector[DateCronExpr, F] {
    type Raw = RawDateCronExpr

    def selectFrom(expr: DateCronExpr): FieldNode[F] = hlistSelect(expr.raw)

  }

  private[this] abstract class FieldNodeWithAnySelector[A, F <: CronField]
      extends FieldSelector[A, F] {
    type Out[X <: CronField] = FieldNodeWithAny[X]
  }
  private[this] abstract class FullCronFieldNodeWithAnySelector[F <: CronField]
      extends FieldNodeWithAnySelector[CronExpr, F] {
    type Raw = RawCronExpr

    def selectFrom(expr: CronExpr): FieldNodeWithAny[F] = hlistSelect(expr.raw)
  }
  private[this] abstract class DateCronFieldNodeWithAnySelector[F <: CronField]
      extends FieldNodeWithAnySelector[DateCronExpr, F] {
    type Raw = RawDateCronExpr

    def selectFrom(expr: DateCronExpr): FieldNodeWithAny[F] = hlistSelect(expr.raw)
  }
}
