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

import cats.Show
import cats.data.NonEmptyList
import cats.instances.all._
import cats.syntax.show._

import cron4s.{CronField, CronUnit}
import cron4s.base._
import cron4s.syntax.field._
import cron4s.syntax.predicate._

/**
  * Generic representation of the expression node for a given field
  *
  * @author Antonio Alonso Dominguez
  */
sealed trait Node[F <: CronField] {

  /**
    * Unit of this expression
    */
  val unit: CronUnit[F]

  def range: IndexedSeq[Int]

}

final class EachNode[F <: CronField] private (val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] = unit.range

  override val toString = "*"

}

object EachNode {

  @inline def apply[F <: CronField](implicit unit: CronUnit[F]): EachNode[F] = new EachNode(unit)

  implicit def eachNodeShow[F <: CronField]: Show[EachNode[F]] =
    Show.fromToString[EachNode[F]]

  implicit def eachNodeInstance[F <: CronField]: FieldExpr[EachNode, F] =
    new FieldExpr[EachNode, F] {
      def unit(node: EachNode[F]): CronUnit[F] = node.unit

      def implies[EE[_ <: CronField]](node: EachNode[F])(ee: EE[F])
          (implicit EE: FieldExpr[EE, F]): Boolean = true

      def matches(node: EachNode[F]): Predicate[Int] = Predicate { x =>
        x >= min(node) && x <= max(node)
      }

      def range(node: EachNode[F]): IndexedSeq[Int] = node.range
    }

}

final class AnyNode[F <: CronField] private (val unit: CronUnit[F]) extends Node[F] {

  lazy val range: IndexedSeq[Int] = unit.range

  override def toString: String = "?"

}

object AnyNode {

  @inline def apply[F <: CronField](implicit unit: CronUnit[F]): AnyNode[F] = new AnyNode(unit)

  implicit def anyNodeShow[F <: CronField]: Show[AnyNode[F]] =
    Show.fromToString[AnyNode[F]]

  implicit def anyNodeInstance[F <: CronField]: FieldExpr[AnyNode, F] =
    new FieldExpr[AnyNode, F] {
      def unit(node: AnyNode[F]): CronUnit[F] = node.unit

      def implies[EE[_ <: CronField]](node: AnyNode[F])(ee: EE[F])
        (implicit EE: FieldExpr[EE, F]): Boolean = true

      def matches(node: AnyNode[F]): Predicate[Int] = Predicate { x =>
        x >= min(node) && x <= max(node)
      }

      def range(node: AnyNode[F]): IndexedSeq[Int] = node.range
    }

}

final class ConstNode[F <: CronField] private (val value: Int, val textValue: Option[String], val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] = Vector(value)

  override lazy val toString: String =
    textValue.getOrElse(value.toString)

}

object ConstNode {

  @inline def apply[F <: CronField](value: Int, textValue: Option[String] = None)
                                   (implicit unit: CronUnit[F]): ConstNode[F] = new ConstNode(value, textValue, unit)

  implicit def constNodeShow[F <: CronField]: Show[ConstNode[F]] =
    Show.fromToString[ConstNode[F]]

  implicit def constNodeInstance[F <: CronField]: FieldExpr[ConstNode, F] =
    new FieldExpr[ConstNode, F] {
      def unit(node: ConstNode[F]): CronUnit[F] = node.unit

      def matches(node: ConstNode[F]): Predicate[Int] = equalTo(node.value)

      def implies[EE[_ <: CronField]](node: ConstNode[F])(ee: EE[F])(implicit EE: FieldExpr[EE, F]): Boolean = {
        val range = ee.range
        (range.size == 1) && range.contains(node.value)
      }

      def range(node: ConstNode[F]): IndexedSeq[Int] = node.range
    }

}

final class BetweenNode[F <: CronField] private (val begin: ConstNode[F], val end: ConstNode[F], val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] = {
    val min = Math.min(begin.value, end.value)
    val max = Math.max(begin.value, end.value)
    min to max
  }

  override lazy val toString: String =
    s"${begin.show}-${end.show}"

}

object BetweenNode {

  @inline def apply[F <: CronField](begin: ConstNode[F], end: ConstNode[F])
                                   (implicit unit: CronUnit[F]): BetweenNode[F] = new BetweenNode(begin, end, unit)

  implicit def betweenNodeShow[F <: CronField]: Show[BetweenNode[F]] =
    Show.fromToString[BetweenNode[F]]

  implicit def betweenNodeInstance[F <: CronField]
      (implicit elemExpr: FieldExpr[ConstNode, F]): FieldExpr[BetweenNode, F] =
    new FieldExpr[BetweenNode, F] {

      def unit(node: BetweenNode[F]): CronUnit[F] = node.unit

      def matches(node: BetweenNode[F]): Predicate[Int] = Predicate { x =>
        if (node.begin.value < node.end.value)
          x >= node.begin.value && x <= node.end.value
        else false
      }

      def implies[EE[_ <: CronField]](node: BetweenNode[F])(ee: EE[F])(implicit EE: FieldExpr[EE, F]): Boolean =
        (node.min <= ee.min) && (node.max >= ee.max)

      def range(node: BetweenNode[F]): IndexedSeq[Int] = node.range

    }

}

final class SeveralNode[F <: CronField] private (val values: NonEmptyList[EnumerableNode[F]], val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] =
    values.toList.view.flatMap(_.range).distinct.sorted.toIndexedSeq

  override lazy val toString: String =
    values.map(_.show).toList.mkString(",")

}

object SeveralNode {

  @inline def apply[F <: CronField](head: EnumerableNode[F], tail: EnumerableNode[F]*)
                                   (implicit unit: CronUnit[F]): SeveralNode[F] =
    new SeveralNode(NonEmptyList.of(head, tail: _*), unit)

  implicit def severalNodeShow[F <: CronField]: Show[SeveralNode[F]] =
    Show.fromToString[SeveralNode[F]]

  implicit def severalNodeInstance[F <: CronField]
      (implicit elemExpr: FieldExpr[EnumerableNode, F]): FieldExpr[SeveralNode, F] =
    new FieldExpr[SeveralNode, F] {
      def unit(node: SeveralNode[F]): CronUnit[F] = node.unit

      def matches(node: SeveralNode[F]): Predicate[Int] =
        anyOf(node.values.map(_.matches))

      def implies[EE[_ <: CronField]](node: SeveralNode[F])(ee: EE[F])(implicit EE: FieldExpr[EE, F]): Boolean =
        range(node).containsSlice(ee.range)

      def range(node: SeveralNode[F]): IndexedSeq[Int] = node.range

    }

}

final class EveryNode[F <: CronField] private (val base: DivisibleNode[F], val freq: Int, val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] = {
    val elements = Stream.iterate[Option[(Int, Int)]](Some(base.min -> 0)) {
      _.flatMap { case (v, _) => base.step(v, freq) }
    }.flatten.takeWhile(_._2 < 1).map(_._1)

    elements.toVector
  }

  override lazy val toString: String =
    s"${base.show}/$freq"

}

object EveryNode {

  @inline def apply[F <: CronField](base: DivisibleNode[F], freq: Int)
                                   (implicit unit: CronUnit[F]): EveryNode[F] = new EveryNode(base, freq, unit)

  implicit def everyNodeShow[F <: CronField]: Show[EveryNode[F]] =
    Show.fromToString[EveryNode[F]]

  implicit def everyNodeInstance[F <: CronField]
      (implicit baseExpr: FieldExpr[DivisibleNode, F]): FieldExpr[EveryNode, F] =
    new FieldExpr[EveryNode, F] {

      def unit(node: EveryNode[F]): CronUnit[F] = node.unit

      def matches(node: EveryNode[F]): Predicate[Int] =
        anyOf(range(node).map(equalTo(_)).toList)

      def implies[EE[_ <: CronField]](node: EveryNode[F])(ee: EE[F])(implicit EE: FieldExpr[EE, F]): Boolean =
        range(node).containsSlice(ee.range)

      def range(node: EveryNode[F]): IndexedSeq[Int] = node.range

    }

}
