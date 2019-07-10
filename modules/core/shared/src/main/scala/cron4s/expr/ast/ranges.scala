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

import cron4s.base._
import cron4s.syntax.circularTraverse._
import cron4s.syntax.productive._
import cron4s.syntax.predicate

sealed trait ComposableRange[F <: CronField]
object ComposableRange {
  implicit def composableRangeProductive[F <: CronField]: Productive[ComposableRange[F], Int] =
    Productive.instance {
      case const: ConstValue[F]     => const.unfold
      case bounded: BoundedRange[F] => bounded.unfold
    }
}

sealed trait DivisibleRange[F <: CronField]
object DivisibleRange {
  implicit def divisibleRangeProductive[F <: CronField]: Productive[DivisibleRange[F], Int] =
    Productive.instance {
      case each: EachInRange[F] => each.unfold
      case bounded: BoundedRange[F] => bounded.unfold
      case enumerated: EnumeratedRange[F] => enumerated.unfold
    }
}

final case class EachInRange[F <: CronField](unit: CronUnit[F]) extends DivisibleRange[F]
object EachInRange {

  implicit def eachInRangeHasCronUnit[F <: CronField]: HasCronUnit[EachInRange[F], F] =
    HasCronUnit.instance(_.unit)

  implicit def eachInRangeFieldExpr[F <: CronField]: FieldExpr[EachInRange, F] = 
    new FieldExpr[EachInRange, F] {
      def matches(range: EachInRange[F]): Predicate[Int] = {
        Predicate { x =>
          x >= range.unit.min && x <= range.unit.max
        }
      }

      def implies[R[_ <: CronField]](range: EachInRange[F])(other: R[F])(
        implicit R: FieldExpr[R, F]
      ): Boolean = true

      def unfold(range: EachInRange[F]): NonEmptyVector[Int] =
        range.unit.values
    }

}

final case class AnyInRange[F <: CronField](unit: CronUnit[F])
object AnyInRange {

  implicit def anyInRangeHasCronUnit[F <: CronField]: HasCronUnit[AnyInRange[F], F] =
    HasCronUnit.instance(_.unit)

  implicit def anyInRangeFieldExpr[F <: CronField]: FieldExpr[AnyInRange, F] = 
    new FieldExpr[AnyInRange, F] {
      def matches(range: AnyInRange[F]): Predicate[Int] = {
        Predicate { x =>
          x >= range.unit.min && x <= range.unit.max
        }
      }

      def implies[R[_ <: CronField]](range: AnyInRange[F])(other: R[F])(
        implicit R: FieldExpr[R, F]
      ): Boolean = true

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

  implicit def constValueHasCronUnit[F <: CronField]: HasCronUnit[ConstValue[F], F] =
    HasCronUnit.instance(_.unit)

  implicit def constValueFieldExpr[F <: CronField]: FieldExpr[ConstValue, F] = 
    new FieldExpr[ConstValue, F] {
      def matches(range: ConstValue[F]): Predicate[Int] =
        predicate.equalTo(range.value)

      def implies[R[_ <: CronField]](range: ConstValue[F])(other: R[F])(
        implicit R: FieldExpr[R, F]
      ): Boolean = {
        val otherValues = R.unfold(other)
        (otherValues.size == 1) && (otherValues.toVector.contains(range.value))
      }

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

  implicit def boundedRangeHasCronUnit[F <: CronField]: HasCronUnit[BoundedRange[F], F] =
    HasCronUnit.instance(_.begin.unit)

  implicit def boundedRangeFieldExpr[F <: CronField]: FieldExpr[BoundedRange, F] = 
    new FieldExpr[BoundedRange, F] {
      def matches(range: BoundedRange[F]): Predicate[Int] = Predicate { x =>
        x >= range.begin.value && x <= range.end.value
      }

      def implies[R[_ <: CronField]](range: BoundedRange[F])(other: R[F])(
        implicit R: FieldExpr[R, F]
      ): Boolean = {
        val otherValues = R.unfold(other)
        range.begin.value <= otherValues.minimum && range.end.value >= otherValues.maximum
      }

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

  implicit def enumeratedRangeHasCronUnit[F <: CronField]: HasCronUnit[EnumeratedRange[F], F] =
    HasCronUnit.instance(_.unit)

  implicit def enumeratedRangeProductive[F <: CronField](
      implicit P: Productive[ComposableRange[F], Int]
  ): Productive[EnumeratedRange[F], Int] =
    Productive.instance { range =>
      NonEmptyVector.fromVectorUnsafe(range.elements.flatMap(P.unfold(_).toNonEmptyList).toList.toVector.distinct.sorted)
    }

  implicit def enumeratedRangeFieldExpr[F <: CronField]: FieldExpr[EnumeratedRange, F] = 
    new FieldExpr[EnumeratedRange, F] {
      def matches(range: EnumeratedRange[F]): Predicate[Int] =
        predicate.anyOf(range.elements.map(_.matches))

      def implies[R[_ <: CronField]](range: EnumeratedRange[F])(other: R[F])(
        implicit R: FieldExpr[R, F]
      ): Boolean =
        range.unfold.toVector.containsSlice(R.unfold(other).toVector)

      def unfold(range: EnumeratedRange[F]): NonEmptyVector[Int] =
        range.values
    }

}

final case class SteppingRange[F <: CronField](
    base: DivisibleRange[F],
    step: Int,
    unit: CronUnit[F]
) {

  lazy val values: NonEmptyVector[Int] = {
    val baseRange  = base.unfold
    val startValue = baseRange.minimum
    val elements = Stream
      .iterate[(Int, Int)](startValue -> 0) {
        case (v, _) => baseRange.step(v, step)
      }
      .flatten
      .takeWhile(_._2 < 1)
      .map(_._1)

      NonEmptyVector.fromVectorUnsafe(elements.toVector)
  }

}
object SteppingRange {

  implicit def steppingRangeHasCronUnit[F <: CronField]: HasCronUnit[SteppingRange[F], F] =
    HasCronUnit.instance(_.unit)

  implicit def steppingRangeProductive[F <: CronField](
      implicit
      P: Productive[DivisibleRange[F], Int]
  ): Productive[SteppingRange[F], Int] =
    Productive.instance { range =>
      val baseRange  = P.unfold(range.base)
      val startValue = baseRange.minimumOption.map(_ -> 0)
      val elements = Stream
        .iterate[Option[(Int, Int)]](startValue) {
          _.flatMap { case (v, _) => Some(baseRange.step(v, range.step)) }
        }
        .flatten
        .takeWhile(_._2 < 1)
        .map(_._1)

      elements.toVector
    }

}
