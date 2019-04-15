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
package expr

import cats.data.NonEmptyList

import cron4s.base._
import cron4s.datetime.IsDateTime

sealed trait CronRange[F <: CronField] {
  def unit: CronUnit[F]
}
sealed trait ComposableRange[F <: CronField] extends CronRange[F]
sealed trait DivisibleRange[F <: CronField]  extends CronRange[F]

case class EachInRange[F <: CronField](unit: CronUnit[F])
    extends CronRange[F] with DivisibleRange[F]
object EachInRange {

  implicit def eachInRangeRange[F <: CronField](
      implicit R: Enumerated[CronUnit[F]]
  ): Enumerated[EachInRange[F]] = new Enumerated[EachInRange[F]] {
    def range(c: EachInRange[F]) = R.range(c.unit)
  }

}

case class AnyInRange[F <: CronField](unit: CronUnit[F]) extends CronRange[F]
object AnyInRange {
  implicit def anyInRangeRange[F <: CronField](
      implicit R: Enumerated[CronUnit[F]]
  ): Enumerated[AnyInRange[F]] = new Enumerated[AnyInRange[F]] {
    def range(c: AnyInRange[F]) = R.range(c.unit)
  }
}

case class ConstValue[F <: CronField](value: Int, unit: CronUnit[F])
    extends CronRange[F] with ComposableRange[F]
object ConstValue {
  implicit def constValueRange[F <: CronField](
      implicit R: Enumerated[CronUnit[F]]
  ): Enumerated[ConstValue[F]] = new Enumerated[ConstValue[F]] {
    def range(c: ConstValue[F]) = Vector(c.value)
  }
}

case class BoundedRange[F <: CronField](
    begin: ConstValue[F],
    end: ConstValue[F],
    unit: CronUnit[F]
) extends CronRange[F] with ComposableRange[F] with DivisibleRange[F]
object BoundedRange {
  implicit def BoundedRangeRange[F <: CronField](
      implicit R: Enumerated[CronUnit[F]]
  ): Enumerated[BoundedRange[F]] = new Enumerated[BoundedRange[F]] {
    def range(c: BoundedRange[F]) = {
      val min = Math.min(c.begin.value, c.end.value)
      val max = Math.max(c.begin.value, c.end.value)
      min to max
    }
  }
}

case class EnumeratedRange[F <: CronField](
    head: ComposableRange[F],
    tail: NonEmptyList[ComposableRange[F]],
    unit: CronUnit[F]
) extends CronRange[F] with DivisibleRange[F] {
  lazy val values: NonEmptyList[ComposableRange[F]] = head :: tail
}
object EnumeratedRange {
  implicit def EnumeratedRangeRange[F <: CronField](
      implicit R: Enumerated[ComposableRange[F]]
  ): Enumerated[EnumeratedRange[F]] = new Enumerated[EnumeratedRange[F]] {
    def range(c: EnumeratedRange[F]) =
      c.values.toList.view.flatMap(_.range).distinct.sorted.toIndexedSeq
  }
}

case class SteppingRange[F <: CronField](
    base: DivisibleRange[F],
    step: Int,
    unit: CronUnit[F]
) extends CronRange[F]
object SteppingRange {
  implicit def SteppingRangeRange[F <: CronField](
      implicit R: Enumerated[DivisibleRange[F]]
  ): Enumerated[SteppingRange[F]] = new Enumerated[SteppingRange[F]] {
    def range(c: SteppingRange[F]) = {
      val elements = Stream
        .iterate[Either[StepError, (Int, Int)]](Right(R.min(c.base) -> 0)) {
          _.flatMap { case (v, _) => R.step(c.base)(v, c.step) }
        }
        .map(_.toOption)
        .flatten
        .takeWhile(_._2 < 1)
        .map(_._1)

      elements.toVector
    }
  }
}

// Nodes

sealed trait CronNode[F <: CronField]

case class RangeNode[F <: CronField](CronRange: CronRange[F]) extends CronNode[F]
object RangeNode {

  implicit def nodeSteppable[F <: CronField, DT](
      implicit S: Steppable[CronRange[F], Int],
      DT: IsDateTime[DT]
  ) = new Steppable[RangeNode[F], DT] {
    def step(node: RangeNode[F], from: DT, step: Step): Either[StepError, (DT, Int)] =
      for {
        currValue             <- DT.get(from, node.CronRange.unit.field)
        (newValue, carryOver) <- S.step(node.CronRange, currValue, step)
        newResult             <- DT.set(from, node.CronRange.unit.field, newValue)
      } yield (newResult, carryOver)
  }

}

case object LastDayOfMonth                     extends CronNode[CronField.DayOfMonth]
case class NthDayOfWeek(nth: Int)              extends CronNode[CronField.DayOfWeek]
case class NthDayOfMonth(nth: Int)             extends CronNode[CronField.DayOfMonth]
case class NthDayOnMthWeek(nth: Int, mth: Int) extends CronNode[CronField.DayOfMonth]
