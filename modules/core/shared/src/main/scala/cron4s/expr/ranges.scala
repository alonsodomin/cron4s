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

import cats.{Eq, Show}
import cats.data.{NonEmptyList, NonEmptyVector}
import cats.implicits._

import cron4s.internal.base._
import cron4s.internal.syntax.all._

sealed trait CronRange[F <: CronField]
sealed trait ComposableRange[F <: CronField] extends CronRange[F]
sealed trait DivisibleRange[F <: CronField]  extends CronRange[F]

final case class EachInRange[F <: CronField](unit: CronUnit[F]) extends DivisibleRange[F]
object EachInRange {

  implicit def eachInRangeHasUnit[F <: CronField]: HasCronUnit[EachInRange[F], F] =
    HasCronUnit.instance(_.unit)

  implicit def eachInRangeHasMatcher[F <: CronField]: HasMatcher[EachInRange[F], Int] =
    HasMatcher.instance(_ => Predicate.always(true))

  implicit def eachInRangeImplies[F <: CronField]: Implies[EachInRange[F], F] =
    new Implies[EachInRange[F], F] {
      @inline
      def implies[B](range: EachInRange[F])(b: B)(
          implicit
          ev: FieldIndexed[EachInRange[F], F],
          indexedB: FieldIndexed[B, F]
      ): Boolean = true
    }

  implicit def eachInRangeProductive[F <: CronField]: Productive[EachInRange[F], Int] =
    Productive.instance(_.unit.values)

  implicit def eachInRangeEq[F <: CronField]: Eq[EachInRange[F]] =
    Eq.fromUniversalEquals

  implicit def eachInRangeShow[F <: CronField]: Show[EachInRange[F]] =
    Show.show(_ => "*")

}

final case class AnyInRange[F <: CronField](unit: CronUnit[F]) extends CronRange[F]
object AnyInRange {

  implicit def anyInRangeHasUnit[F <: CronField]: HasCronUnit[AnyInRange[F], F] =
    HasCronUnit.instance(_.unit)

  implicit def anyInRangeHasMatcher[F <: CronField]: HasMatcher[AnyInRange[F], Int] =
    HasMatcher.instance(_ => Predicate.always(true))

  implicit def anyInRangeImplies[F <: CronField]: Implies[AnyInRange[F], F] =
    new Implies[AnyInRange[F], F] {
      @inline
      def implies[B](range: AnyInRange[F])(b: B)(
          implicit
          ev: FieldIndexed[AnyInRange[F], F],
          indexedB: FieldIndexed[B, F]
      ): Boolean = true
    }

  implicit def anyInRangeProductive[F <: CronField]: Productive[AnyInRange[F], Int] =
    Productive.instance(_.unit.values)

  implicit def anyInRangeEq[F <: CronField]: Eq[AnyInRange[F]] =
    Eq.fromUniversalEquals

  implicit def anyInRangeShow[F <: CronField]: Show[AnyInRange[F]] =
    Show.show(_ => "?")

}

final case class ConstValue[F <: CronField](
    value: Int,
    textValue: Option[String],
    unit: CronUnit[F]
) extends ComposableRange[F]
object ConstValue {

  implicit def constValueHasCronUnit[F <: CronField]: HasCronUnit[ConstValue[F], F] =
    HasCronUnit.instance(_.unit)

  implicit def constValueHasMatcher[F <: CronField]: HasMatcher[ConstValue[F], Int] =
    HasMatcher.instance(range => Predicate.equalTo(range.value))

  implicit def constValueImplies[F <: CronField, O[_ <: CronField]](
      implicit productiveO: Productive[O[F], Int]
  ): Implies[ConstValue[F], F] =
    new Implies[ConstValue[F], F] {
      def implies[B](range: ConstValue[F])(b: B)(
          implicit
          ev: FieldIndexed[ConstValue[F], F],
          indexedB: FieldIndexed.Aux[B, F, O]
      ): Boolean = {
        val otherValues = productiveO.unfold(indexedB.cast(b))
        (otherValues.size == 1) && (otherValues.toVector.contains(range.value))
      }
    }

  implicit def constValueProductive[F <: CronField]: Productive[ConstValue[F], Int] =
    Productive.instance(range => NonEmptyVector.of(range.value))

  implicit def constValueEq[F <: CronField]: Eq[ConstValue[F]] =
    Eq.fromUniversalEquals

  implicit def constValueShow[F <: CronField]: Show[ConstValue[F]] =
    Show.show(x => x.textValue.getOrElse(x.value.toString))

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

  implicit def boundedRangeHasUnit[F <: CronField]: HasCronUnit[BoundedRange[F], F] =
    HasCronUnit.instance(_.begin.unit)

  implicit def boundedRangeHasMatcher[F <: CronField]: HasMatcher[BoundedRange[F], Int] =
    HasMatcher.instance(
      range =>
        Predicate { x =>
          x >= range.begin.value && x <= range.end.value
      }
    )

  implicit def boundedRangeImplies[F <: CronField, O[_ <: CronField]](
      implicit productiveO: Productive[O[F], Int]
  ): Implies[BoundedRange[F], F] =
    new Implies[BoundedRange[F], F] {
      def implies[B](range: BoundedRange[F])(b: B)(
          implicit
          ev: FieldIndexed[BoundedRange[F], F],
          indexedB: FieldIndexed.Aux[B, F, O]
      ): Boolean = {
        val otherValues = productiveO.unfold(indexedB.cast(b))
        range.begin.value <= otherValues.minimum && range.end.value >= otherValues.maximum
      }
    }

  implicit def boundedRangeProductive[F <: CronField]: Productive[BoundedRange[F], Int] =
    Productive.instance(_.values)

  implicit def boundedRangeEq[F <: CronField]: Eq[BoundedRange[F]] =
    Eq.fromUniversalEquals

  implicit def boundedRangeShow[F <: CronField]: Show[BoundedRange[F]] =
    Show.show(x => show"${x.begin}-${x.end}")

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

  implicit def enumeratedRangeHasMatcher[F <: CronField]: HasMatcher[EnumeratedRange[F], Int] =
    HasMatcher.instance(range => Predicate.anyOf(range.elements.map(_.matches)))

  implicit def enumeratedRangeProductive[F <: CronField]: Productive[EnumeratedRange[F], Int] =
    Productive.instance(_.values)

  implicit def enumeratedRangeImplies[F <: CronField, O[_ <: CronField]](
      implicit
      productiveRange: Productive[EnumeratedRange[F], Int],
      productiveO: Productive[O[F], Int]
  ): Implies[EnumeratedRange[F], F] = new Implies[EnumeratedRange[F], F] {
    def implies[B](range: EnumeratedRange[F])(b: B)(
        implicit
        ev: FieldIndexed[EnumeratedRange[F], F],
        indexedB: FieldIndexed.Aux[B, F, O]
    ): Boolean = {
      val otherValues = productiveO.unfold(indexedB.cast(b))
      productiveRange.unfold(range).toVector.containsSlice(otherValues.toVector)
    }
  }

  implicit def enumeratedRangeEq[F <: CronField]: Eq[EnumeratedRange[F]] =
    Eq.fromUniversalEquals

  implicit def enumeratedRangeShow[F <: CronField]: Show[EnumeratedRange[F]] = {
    import cats.derived.auto.show._
    Show.show(x => x.elements.map(_.show).toList.mkString(","))
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

  implicit def steppingRangeHasCronUnit[F <: CronField]: HasCronUnit[SteppingRange[F], F] =
    HasCronUnit.instance(_.unit)

  implicit def steppingRangeHasMatcher[F <: CronField]: HasMatcher[SteppingRange[F], Int] =
    HasMatcher.instance { range =>
      val preds: NonEmptyVector[Predicate[Int]] =
        range.values.map((x: Int) => Predicate.equalTo(x))
      Predicate.anyOf(preds)
    }

  implicit def steppingRangeProductive[F <: CronField]: Productive[SteppingRange[F], Int] =
    Productive.instance(_.values)

  implicit def steppingRangeImplies[F <: CronField, O[_ <: CronField]](
      implicit
      productiveRange: Productive[SteppingRange[F], Int],
      productiveO: Productive[O[F], Int]
  ): Implies[SteppingRange[F], F] = new Implies[SteppingRange[F], F] {
    def implies[B](range: SteppingRange[F])(b: B)(
        implicit
        ev: FieldIndexed[SteppingRange[F], F],
        indexedB: FieldIndexed.Aux[B, F, O]
    ): Boolean = {
      val otherValues = productiveO.unfold(indexedB.cast(b))
      productiveRange.unfold(range).toVector.containsSlice(otherValues.toVector)
    }
  }

  implicit def steppingRangeEq[F <: CronField]: Eq[SteppingRange[F]] =
    Eq.fromUniversalEquals

  implicit def steppingRangeShow[F <: CronField]: Show[SteppingRange[F]] = {
    import cats.derived.auto.show._
    Show.show(x => show"${x.base}/${x.step}")
  }

}
