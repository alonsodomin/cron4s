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

import cron4s.{CronField, CronUnit}
import cron4s.base._
import cron4s.syntax.predicate._

import scalaz.NonEmptyList
import scalaz.std.anyVal._
import scalaz.std.list._
import scalaz.syntax.show._

/**
  * Generic representation of the expression node for a given field
  *
  * @author Antonio Alonso Dominguez
  */
sealed trait Node[+F <: CronField] {

  /**
    * Unit of this expression
    */
  val unit: CronUnit[F]

  def range: IndexedSeq[Int]

}

final case class EachNode[+F <: CronField](implicit val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] = unit.range

}

object EachNode {

  implicit def eachNodeInstance[F <: CronField]: Expr[EachNode, F] =
    new Expr[EachNode, F] {
      def unit(node: EachNode[F]): CronUnit[F] = node.unit

      def matches(node: EachNode[F]): Predicate[Int] = Predicate { x =>
        x >= min(node) && x <= max(node)
      }

      override def shows(node: EachNode[F]): String = "*"

      def range(node: EachNode[F]): IndexedSeq[Int] = node.range
    }

}

final case class ConstNode[F <: CronField]
    (value: Int, textValue: Option[String] = None)
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] = Vector(value)

}

object ConstNode {

  implicit def constNodeInstance[F <: CronField]: Expr[ConstNode, F] =
    new Expr[ConstNode, F] {
      def unit(node: ConstNode[F]): CronUnit[F] = node.unit
      def matches(node: ConstNode[F]): Predicate[Int] = equalTo(node.value)
      def range(node: ConstNode[F]): IndexedSeq[Int] = node.range
      override def shows(node: ConstNode[F]): String = node.textValue.getOrElse(node.value.toString)
    }

}

final case class BetweenNode[F <: CronField]
    (begin: ConstNode[F], end: ConstNode[F])
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] = {
    if (begin.value < end.value) begin.value to end.value
    else Vector.empty
  }

}

object BetweenNode {

  implicit def betweenNodeInstance[F <: CronField]
      (implicit elemExpr: Expr[ConstNode, F]): Expr[BetweenNode, F] =
    new Expr[BetweenNode, F] {

      def unit(node: BetweenNode[F]): CronUnit[F] = node.unit

      def matches(node: BetweenNode[F]): Predicate[Int] = Predicate { x =>
        if (node.begin.value < node.end.value)
          x >= node.begin.value && x <= node.end.value
        else false
      }

      def range(node: BetweenNode[F]): IndexedSeq[Int] = node.range

      override def shows(node: BetweenNode[F]): String =
        s"${node.begin.shows}-${node.end.shows}"
    }

}

final case class SeveralNode[F <: CronField]
    (values: NonEmptyList[EnumerableNode[F]])
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] =
    values.list.toVector.view.flatMap(_.range).distinct.sorted.toIndexedSeq

}

object SeveralNode {

  def apply[F <: CronField](head: EnumerableNode[F], tail: EnumerableNode[F]*)
                           (implicit unit: CronUnit[F]): SeveralNode[F] =
    SeveralNode(NonEmptyList(head, tail: _*))

  implicit def severalNodeInstance[F <: CronField]
      (implicit elemExpr: Expr[EnumerableNode, F]): Expr[SeveralNode, F] =
    new Expr[SeveralNode, F] {
      def unit(node: SeveralNode[F]): CronUnit[F] = node.unit

      def matches(node: SeveralNode[F]): Predicate[Int] =
        anyOf(node.values.map(_.matches))

      def range(node: SeveralNode[F]): IndexedSeq[Int] = node.range

      override def shows(node: SeveralNode[F]): String =
        node.values.map(_.shows).list.toList.mkString(",")
    }

}

final case class EveryNode[F <: CronField]
    (base: DivisibleNode[F], freq: Int)
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] = {
    val elements = Stream.iterate[Option[(Int, Int)]](Some(base.min -> 0)) {
      _.flatMap { case (v, _) => base.step(v, freq) }
    }.flatten.takeWhile(_._2 < 1).map(_._1)

    elements.toVector
  }

}

object EveryNode {

  implicit def everyNodeInstance[F <: CronField]
      (implicit baseExpr: Expr[DivisibleNode, F]): Expr[EveryNode, F] =
    new Expr[EveryNode, F] {

      def unit(node: EveryNode[F]): CronUnit[F] = node.unit

      def matches(node: EveryNode[F]): Predicate[Int] =
        anyOf(range(node).map(equalTo(_)).toList)

      def range(node: EveryNode[F]): IndexedSeq[Int] = node.range

      override def shows(node: EveryNode[F]): String =
        s"${node.base.shows}/${node.freq}"

    }

}