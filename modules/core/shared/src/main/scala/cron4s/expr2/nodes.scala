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

sealed trait Constraint[F <: CronField] {
  def unit: CronUnit[F]
}
sealed trait EnumerableConstraint[F <: CronField] extends Constraint[F]
sealed trait DivisibleConstraint[F <: CronField]  extends Constraint[F]

case class EachConstraint[F <: CronField](unit: CronUnit[F])
    extends Constraint[F] with DivisibleConstraint[F]
object EachConstraint {

  implicit def eachConstraintRange[F <: CronField](
      implicit R: Enumerated[CronUnit[F]]
  ): Enumerated[EachConstraint[F]] = new Enumerated[EachConstraint[F]] {
    def range(c: EachConstraint[F]) = R.range(c.unit)
  }

}

case class AnyConstraint[F <: CronField](unit: CronUnit[F]) extends Constraint[F]
object AnyConstraint {
  implicit def anyConstraintRange[F <: CronField](
      implicit R: Enumerated[CronUnit[F]]
  ): Enumerated[AnyConstraint[F]] = new Enumerated[AnyConstraint[F]] {
    def range(c: AnyConstraint[F]) = R.range(c.unit)
  }
}

case class ConstConstraint[F <: CronField](value: Int, unit: CronUnit[F])
    extends Constraint[F] with EnumerableConstraint[F]
object ConstConstraint {
  implicit def constConstraintRange[F <: CronField](
      implicit R: Enumerated[CronUnit[F]]
  ): Enumerated[ConstConstraint[F]] = new Enumerated[ConstConstraint[F]] {
    def range(c: ConstConstraint[F]) = Vector(c.value)
  }
}

case class BetweenConstraint[F <: CronField](
    begin: ConstConstraint[F],
    end: ConstConstraint[F],
    unit: CronUnit[F]
) extends Constraint[F] with EnumerableConstraint[F] with DivisibleConstraint[F]
object BetweenConstraint {
  implicit def betweenConstraintRange[F <: CronField](
      implicit R: Enumerated[CronUnit[F]]
  ): Enumerated[BetweenConstraint[F]] = new Enumerated[BetweenConstraint[F]] {
    def range(c: BetweenConstraint[F]) = {
      val min = Math.min(c.begin.value, c.end.value)
      val max = Math.max(c.begin.value, c.end.value)
      min to max
    }
  }
}

case class SeveralConstraint[F <: CronField](
    head: EnumerableConstraint[F],
    tail: NonEmptyList[EnumerableConstraint[F]],
    unit: CronUnit[F]
) extends Constraint[F] with DivisibleConstraint[F] {
  lazy val values: NonEmptyList[EnumerableConstraint[F]] = head :: tail
}
object SeveralConstraint {
  implicit def severalConstraintRange[F <: CronField](
      implicit R: Enumerated[EnumerableConstraint[F]]
  ): Enumerated[SeveralConstraint[F]] = new Enumerated[SeveralConstraint[F]] {
    def range(c: SeveralConstraint[F]) =
      c.values.toList.view.flatMap(_.range).distinct.sorted.toIndexedSeq
  }
}

case class StepWiseConstraint[F <: CronField](
    base: DivisibleConstraint[F],
    step: Int,
    unit: CronUnit[F]
) extends Constraint[F]
object StepWiseConstraint {
  implicit def stepWiseConstraintRange[F <: CronField](
      implicit R: Enumerated[DivisibleConstraint[F]]
  ): Enumerated[StepWiseConstraint[F]] = new Enumerated[StepWiseConstraint[F]] {
    def range(c: StepWiseConstraint[F]) = {
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

case class RangeNode[F <: CronField](constraint: Constraint[F]) extends CronNode[F]
object RangeNode {

  implicit def nodeSteppable[F <: CronField, DT](
      implicit S: Steppable[Constraint[F], Int],
      DT: IsDateTime[DT]
  ) = new Steppable[RangeNode[F], DT] {
    def step(node: RangeNode[F], from: DT, step: Step): Either[StepError, (DT, Int)] =
      for {
        currValue             <- DT.get(from, node.constraint.unit.field)
        (newValue, carryOver) <- S.step(node.constraint, currValue, step)
        newResult             <- DT.set(from, node.constraint.unit.field, newValue)
      } yield (newResult, carryOver)
  }

}

case object LastDayOfMonth extends CronNode[CronField.DayOfMonth]
case class NthDayOfWeek(nth: Int) extends CronNode[CronField.DayOfWeek]
case class NthDayOfMonth(nth: Int) extends CronNode[CronField.DayOfMonth]
case class NthDayOnMthWeek(nth: Int, mth: Int) extends CronNode[CronField.DayOfMonth]
