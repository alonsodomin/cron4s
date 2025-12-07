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

import cats.data.NonEmptyList

package parser {

  trait Parser {
    def parse(input: String): Either[Error, CronExpr]
  }

  sealed trait CronUnit
  object CronUnit {
    sealed trait Second extends CronUnit
    case object Second  extends Second

    sealed trait Minute extends CronUnit
    case object Minute  extends Minute

    sealed trait Hour extends CronUnit
    case object Hour  extends Hour

    sealed trait DayOfMonth extends CronUnit
    case object DayOfMonth  extends DayOfMonth

    sealed trait Month extends CronUnit
    case object Month  extends Month

    sealed trait DayOfWeek extends CronUnit
    case object DayOfWeek  extends DayOfWeek

    sealed trait Year extends CronUnit
    case object Year  extends Year

    final val All: List[CronUnit] =
      List(Second, Minute, Hour, DayOfMonth, Month, DayOfWeek, Year)
  }

  sealed trait Node

  object Node {
    sealed trait EnumerableNode
    sealed trait DivisibleNode
    sealed trait NodeWithoutAny

    case object EachNode extends Node with NodeWithoutAny with DivisibleNode {
      override val toString = "*"
    }

    case object AnyNode extends Node {
      override def toString: String = "?"
    }

    final case class ConstNode(
        value: Int,
        textValue: Option[String] = None
    ) extends Node with NodeWithoutAny with EnumerableNode {
      override lazy val toString: String = textValue.getOrElse(value.toString)
    }

    final case class BetweenNode(
        begin: ConstNode,
        end: ConstNode
    ) extends Node with NodeWithoutAny with EnumerableNode with DivisibleNode {
      override lazy val toString: String = s"$begin-$end"
    }

    final case class SeveralNode(
        head: EnumerableNode,
        tail: NonEmptyList[EnumerableNode]
    ) extends Node with NodeWithoutAny with DivisibleNode {
      lazy val values: List[EnumerableNode] = head :: tail.toList
      override lazy val toString: String    = values.mkString(",")
    }

    object SeveralNode {
      @inline def apply(
          first: EnumerableNode,
          second: EnumerableNode,
          tail: EnumerableNode*
      ): SeveralNode =
        new SeveralNode(first, NonEmptyList.of(second, tail: _*))

      def fromSeq(xs: Seq[EnumerableNode]): Option[SeveralNode] = {
        def splitSeq(
            xs: Seq[EnumerableNode]
        ): Option[(EnumerableNode, EnumerableNode, Seq[EnumerableNode])] =
          if (xs.length < 2) None
          else Some((xs.head, xs.tail.head, xs.tail.tail))

        splitSeq(xs).map {
          case (first, second, tail) => SeveralNode(first, second, tail: _*)
        }
      }

    }

    final case class EveryNode(
        base: DivisibleNode,
        freq: Int
    ) extends Node with NodeWithoutAny {
      override lazy val toString: String = s"$base/$freq"
    }
  }

  object Months {
    val textValues = IndexedSeq(
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
  object DaysOfWeek {
    val textValues = IndexedSeq("mon", "tue", "wed", "thu", "fri", "sat", "sun")
  }

  final case class CronExpr(
      seconds: Node.NodeWithoutAny,
      minutes: Node.NodeWithoutAny,
      hours: Node.NodeWithoutAny,
      daysOfMonth: Node,
      months: Node.NodeWithoutAny,
      daysOfWeek: Node,
      year: Option[Node.NodeWithoutAny] = None
  )

  sealed abstract class Error(description: String) extends Exception(description)

  case object ExprTooShort extends Error("The provided expression was too short")

  final case class ParseFailed(expected: String, position: Int, found: Option[String])
      extends Error(s"$expected at position ${position}${found.fold("")(f => s" but found '$f'")}")

  object ParseFailed {
    def apply(expected: String, position: Int, found: Option[String] = None): ParseFailed =
      new ParseFailed(expected, position, found)

    @deprecated("Use the other apply method signature with optional 'found'", "0.6.1")
    def apply(msg: String, found: String, position: Int): ParseFailed =
      ParseFailed(msg, position, Some(found))
  }

}
