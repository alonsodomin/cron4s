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
package ast

import cats.data.{NonEmptyList, NonEmptyVector}
import cats.implicits._

import cron4s.internal.base._
import cron4s.internal.expr._
import cron4s.internal.syntax.all._

sealed trait CronRange[F <: CronField]
sealed trait ComposableRange[F <: CronField] extends CronRange[F]
sealed trait DivisibleRange[F <: CronField] extends CronRange[F]

final case class EachInRange[F <: CronField](unit: CronUnit[F]) extends DivisibleRange[F]
object EachInRange {

  implicit def eachInRangeRangeExpr[F <: CronField]: RangeExpr[EachInRange, F] = 
    new RangeExpr[EachInRange, F] {
      def matches(range: EachInRange[F]): Predicate[Int] = {
        Predicate { x =>
          x >= range.unit.min && x <= range.unit.max
        }
      }

      def implies[R[_ <: CronField]](range: EachInRange[F])(other: R[F])(
        implicit R: RangeExpr[R, F]
      ): Boolean = true

      def unit(range: EachInRange[F]): CronUnit[F] = range.unit

      def unfold(range: EachInRange[F]): NonEmptyVector[Int] =
        range.unit.values
    }

}

final case class AnyInRange[F <: CronField](unit: CronUnit[F]) extends CronRange[F]
object AnyInRange {

  implicit def anyInRangeRangeExpr[F <: CronField]: RangeExpr[AnyInRange, F] = 
    new RangeExpr[AnyInRange, F] {
      def matches(range: AnyInRange[F]): Predicate[Int] = {
        Predicate { x =>
          x >= range.unit.min && x <= range.unit.max
        }
      }

      def implies[R[_ <: CronField]](range: AnyInRange[F])(other: R[F])(
        implicit R: RangeExpr[R, F]
      ): Boolean = true

      def unit(range: AnyInRange[F]): CronUnit[F] = range.unit

      def unfold(range: AnyInRange[F]): NonEmptyVector[Int] =
        range.unit.values
    }

}

final case class ConstValue[F <: CronField](
    value: Int,
    textValue: Option[String],
    unit: CronUnit[F]
) extends ComposableRange[F]
object ConstValue {

  implicit def constValueRangeExpr[F <: CronField]: RangeExpr[ConstValue, F] = 
    new RangeExpr[ConstValue, F] {
      def matches(range: ConstValue[F]): Predicate[Int] =
        Predicate.equalTo(range.value)

      def implies[R[_ <: CronField]](range: ConstValue[F])(other: R[F])(
        implicit R: RangeExpr[R, F]
      ): Boolean = {
        val otherValues = R.unfold(other)
        (otherValues.size == 1) && (otherValues.toVector.contains(range.value))
      }

      def unit(range: ConstValue[F]): CronUnit[F] = range.unit

      def unfold(range: ConstValue[F]): NonEmptyVector[Int] =
        NonEmptyVector.of(range.value)

    }

}

final case class BoundedRange[F <: CronField](
    begin: ConstValue[F],
    end: ConstValue[F]
) extends ComposableRange[F] with DivisibleRange[F] {

  lazy val values: NonEmptyVector[Int] = {
    val min = Math.min(begin.value, end.value)
    val max = Math.max(begin.value, end.value)
    NonEmptyVector.fromVectorUnsafe((min to max).toVector)
  }

}
object BoundedRange {

  implicit def boundedRangeRangeExpr[F <: CronField]: RangeExpr[BoundedRange, F] = 
    new RangeExpr[BoundedRange, F] {
      def matches(range: BoundedRange[F]): Predicate[Int] = Predicate { x =>
        x >= range.begin.value && x <= range.end.value
      }

      def implies[R[_ <: CronField]](range: BoundedRange[F])(other: R[F])(
        implicit R: RangeExpr[R, F]
      ): Boolean = {
        val otherValues = R.unfold(other)
        range.begin.value <= otherValues.minimum && range.end.value >= otherValues.maximum
      }

      def unit(range: BoundedRange[F]): CronUnit[F] = range.begin.unit

      def unfold(range: BoundedRange[F]): NonEmptyVector[Int] =
        range.values
    }

}

final case class EnumeratedRange[F <: CronField](
    head: ComposableRange[F],
    tail: NonEmptyList[ComposableRange[F]],
    unit: CronUnit[F]
) extends DivisibleRange[F] {
  lazy val elements: NonEmptyList[ComposableRange[F]] = head :: tail

  lazy val values: NonEmptyVector[Int] =
    NonEmptyVector.fromVectorUnsafe {
      elements.flatMap(_.unfold.toNonEmptyList).toList.toVector.distinct.sorted
    }
}
object EnumeratedRange {

  def fromList[F <: CronField](xs: List[ComposableRange[F]])(
      implicit
      unit: CronUnit[F]
  ): Option[EnumeratedRange[F]] = {
    def splitList: Option[(ComposableRange[F], ComposableRange[F], List[ComposableRange[F]])] =
      xs match {
        case x1 :: x2 :: tail => Some((x1, x2, tail))
        case _                => None
      }

    splitList.map {
      case (first, second, tail) => EnumeratedRange(first, NonEmptyList.of(second, tail: _*), unit)
    }
  }

  implicit def enumeratedRangeRangeExpr[F <: CronField]: RangeExpr[EnumeratedRange, F] = 
    new RangeExpr[EnumeratedRange, F] {
      def matches(range: EnumeratedRange[F]): Predicate[Int] =
        Predicate.anyOf(range.elements.map(_.matches))

      def implies[R[_ <: CronField]](range: EnumeratedRange[F])(other: R[F])(
        implicit R: RangeExpr[R, F]
      ): Boolean =
        range.unfold.toVector.containsSlice(R.unfold(other).toVector)

      def unit(range: EnumeratedRange[F]): CronUnit[F] = range.unit

      def unfold(range: EnumeratedRange[F]): NonEmptyVector[Int] =
        range.values
    }

}

final case class SteppingRange[F <: CronField](
    base: DivisibleRange[F],
    step: Int,
    unit: CronUnit[F]
) extends CronRange[F] {

  lazy val values: NonEmptyVector[Int] = {
    val baseRange  = base.unfold
    val startValue = baseRange.minimum
    val elements = Stream
      .iterate[(Int, Int)](startValue -> 0) {
        case (v, _) => baseRange.step(v, step)
      }
      .takeWhile(_._2 < 1)
      .map(_._1)

      NonEmptyVector.fromVectorUnsafe(elements.toVector)
  }

}
object SteppingRange {

  implicit def steppingRangeRangeExpr[F <: CronField]: RangeExpr[SteppingRange, F] = 
    new RangeExpr[SteppingRange, F] {
      def matches(range: SteppingRange[F]): Predicate[Int] = {
        val preds: NonEmptyVector[Predicate[Int]] =
          unfold(range).map((x: Int) => Predicate.equalTo(x))
        Predicate.anyOf(preds)
      }

      def implies[R[_ <: CronField]](range: SteppingRange[F])(other: R[F])(
        implicit R: RangeExpr[R, F]
      ): Boolean =
        unfold(range).toVector.containsSlice(R.unfold(other).toVector)

      def unit(range: SteppingRange[F]): CronUnit[F] = range.unit

      def unfold(range: SteppingRange[F]): NonEmptyVector[Int] =
        range.values
    }

}
