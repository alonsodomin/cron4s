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

import cats.{Eq, Show, Order, Hash}
import cats.data.NonEmptyVector
import cats.implicits._

import cron4s.internal.base.{Enum, Productive}

import scala.annotation.implicitNotFound
import scala.collection.SortedSet

/**
  * A Cron Unit is the representation of valid values that are accepted
  * at a given Cron Field.
  *
  * @author Antonio Alonso Dominguez
  */
@implicitNotFound("Field ${F} is not supported on Cron expressions")
sealed trait CronUnit[+F <: CronField] extends Serializable {

  def min: Int
  def max: Int

  /**
    * @return the CronField for this unit
    */
  def field: F

  /**
    * Cron units have a range of valid values
    *
    * @return the range of valid values
    */
  @deprecated("Use values instead", "0.6.0")
  def range: IndexedSeq[Int] = values.toVector

  def values: NonEmptyVector[Int]

}

object CronUnit extends CronUnits {

  @inline def apply[F <: CronField](implicit unit: CronUnit[F]): CronUnit[F] =
    unit

  final val All: SortedSet[CronUnit[CronField]] =
    SortedSet(Seconds, Minutes, Hours, DaysOfMonth, Months, DaysOfWeek)

}

private[cron4s] trait CronUnits {
  import CronField._

  // $COVERAGE-OFF$
  implicit def cronUnitEq[F <: CronField]: Eq[CronUnit[F]] =
    Eq.fromUniversalEquals[CronUnit[F]]

  implicit def cronUnitShow[F <: CronField]: Show[CronUnit[F]] =
    Show.fromToString[CronUnit[F]]

  // implicit def cronUnitHash[F <: CronField]: Hash[CronUnit[F]] =
  //   Hash.fromUniversalHashCode

  implicit def cronUnitOrder[F <: CronField]: Order[CronUnit[F]] =
    Order.by(CronUnit.All.toVector.indexOf)

  implicit val cronUnitEnum: Enum[CronUnit[CronField]] = Enum.fromSet(CronUnit.All)

  implicit def cronUnitProductive[F <: CronField]: Productive[CronUnit[F], Int] =
    Productive.instance(_.values)
  // $COVERAGE-ON$

  private[cron4s] abstract class AbstractCronUnit[F <: CronField](
      val field: F,
      val min: Int,
      val max: Int
  ) extends CronUnit[F] {

    val values: NonEmptyVector[Int] =
      NonEmptyVector.fromVectorUnsafe((min to max).toVector)

  }

  implicit case object Seconds     extends AbstractCronUnit[Second](Second, 0, 59)
  implicit case object Minutes     extends AbstractCronUnit[Minute](Minute, 0, 59)
  implicit case object Hours       extends AbstractCronUnit[Hour](Hour, 0, 23)
  implicit case object DaysOfMonth extends AbstractCronUnit[DayOfMonth](DayOfMonth, 1, 31)
  implicit case object Months extends AbstractCronUnit[Month](Month, 1, 12) {
    val textValues = Vector(
      "jan",
      "feb",
      "mar",
      "apr",
      "may",
      "jun",
      "jul",
      "ago",
      "sep",
      "oct",
      "nov",
      "dec"
    )
  }
  implicit case object DaysOfWeek extends AbstractCronUnit[DayOfWeek](DayOfWeek, 0, 6) {
    val textValues = Vector("mon", "tue", "wed", "thu", "fri", "sat", "sun")
  }

}

// private[cron4s] trait CronUnitInstances extends CronUnits {
//   import CronField._

//   protected def enumeratedUnit[F <: CronField](unit: CronUnit[F]): Enumerated[CronUnit[F]] =
//     new Enumerated[CronUnit[F]] {
//       override def range(fL: CronUnit[F]): IndexedSeq[Int] = unit.range
//     }

//   implicit val secondsInstance     : Enumerated[CronUnit[Second]]     = enumeratedUnit(Seconds)
//   implicit val minutesInstance     : Enumerated[CronUnit[Minute]]     = enumeratedUnit(Minutes)
//   implicit val hoursInstance       : Enumerated[CronUnit[Hour]]       = enumeratedUnit(Hours)
//   implicit val daysOfMonthInstance : Enumerated[CronUnit[DayOfMonth]] = enumeratedUnit(DaysOfMonth)
//   implicit val monthsInstance      : Enumerated[CronUnit[Month]]      = enumeratedUnit(Months)
//   implicit val daysOfWeekInstance  : Enumerated[CronUnit[DayOfWeek]]  = enumeratedUnit(DaysOfWeek)

// }
