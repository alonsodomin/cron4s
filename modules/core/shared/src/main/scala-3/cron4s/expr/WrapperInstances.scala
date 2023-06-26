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
import cats.{Eq, Show}
import scala.language.implicitConversions
import cron4s.toExprOps
import cron4s.{CronField, CronUnit}
import cron4s.base.Predicate

private[cron4s] trait FieldNodeInstances {
  implicit def fieldNodeEq[F <: CronField]: Eq[FieldNode[F]] =
    Eq.fromUniversalEquals
  implicit def fieldNodeShow[F <: CronField]: Show[FieldNode[F]] =
    Show.fromToString[FieldNode[F]]
  implicit def fieldNodeInstance[F <: CronField]: FieldExpr[FieldNode, F] =
    new FieldExpr[FieldNode, F] {
      def matches(node: FieldNode[F]): Predicate[Int] =
        _root_.cron4s.expr.ops.matches(node.raw)

      def range(node: FieldNode[F]): IndexedSeq[Int] =
        _root_.cron4s.expr.ops.range(node.raw)

      def implies[EE[_ <: CronField]](
          node: FieldNode[F]
      )(ee: EE[F])(implicit EE: FieldExpr[EE, F]): Boolean =
        node.raw match {
          case each: EachNode[F]       => each.implies(ee)
          case const: ConstNode[F]     => const.implies(ee)
          case between: BetweenNode[F] => between.implies(ee)
          case several: SeveralNode[F] => several.implies(ee)
          case every: EveryNode[F]     => every.implies(ee)
          case _                       => sys.error("Impossible!")
        }

      def unit(node: FieldNode[F]): CronUnit[F] =
        _root_.cron4s.expr.ops.unit(node.raw)
    }
}

private[cron4s] trait FieldNodeWithAnyInstances {
  implicit def fieldNodeWithAnyEq[F <: CronField]: Eq[FieldNodeWithAny[F]] =
    Eq.fromUniversalEquals

  implicit def fieldNodeWithAnyShow[F <: CronField]: Show[FieldNodeWithAny[F]] =
    Show.fromToString[FieldNodeWithAny[F]]

  implicit def fieldNodeWithAnyInstance[F <: CronField]: FieldExpr[FieldNodeWithAny, F] =
    new FieldExpr[FieldNodeWithAny, F] {
      def matches(node: FieldNodeWithAny[F]): Predicate[Int] =
        _root_.cron4s.expr.ops.matches(node.raw)

      def range(node: FieldNodeWithAny[F]): IndexedSeq[Int] =
        _root_.cron4s.expr.ops.range(node.raw)

      def implies[EE[_ <: CronField]](
          node: FieldNodeWithAny[F]
      )(ee: EE[F])(implicit EE: FieldExpr[EE, F]): Boolean =
        node.raw match {
          case any: AnyNode[F]       => any.implies(ee)
          case tail: RawFieldNode[F] => new FieldNode[F](tail).implies(ee)
        }

      def unit(node: FieldNodeWithAny[F]): CronUnit[F] =
        _root_.cron4s.expr.ops.unit(node.raw)
    }
}

private[cron4s] trait EnumerableNodeInstances {
  implicit def enumerableNodeEq[F <: CronField]: Eq[EnumerableNode[F]] =
    Eq.fromUniversalEquals

  implicit def enumerableNodeShow[F <: CronField]: Show[EnumerableNode[F]] =
    Show.fromToString[EnumerableNode[F]]

  implicit def enumerableNodeInstance[F <: CronField]: FieldExpr[EnumerableNode, F] =
    new FieldExpr[EnumerableNode, F] {
      def matches(node: EnumerableNode[F]): Predicate[Int] =
        _root_.cron4s.expr.ops.matches(node.raw)

      def implies[EE[_ <: CronField]](
          node: EnumerableNode[F]
      )(ee: EE[F])(implicit EE: FieldExpr[EE, F]): Boolean =
        node.raw match {
          case const: ConstNode[F]     => const.implies(ee)
          case between: BetweenNode[F] => between.implies(ee)
          case _                       => sys.error("Impossible!")
        }

      def range(node: EnumerableNode[F]): IndexedSeq[Int] =
        _root_.cron4s.expr.ops.range(node.raw)

      def unit(node: EnumerableNode[F]): CronUnit[F] =
        _root_.cron4s.expr.ops.unit(node.raw)
    }
}

private[cron4s] trait DivisibleNodeInstances {
  implicit def divisibleNodeEq[F <: CronField]: Eq[DivisibleNode[F]] =
    Eq.fromUniversalEquals

  implicit def divisibleNodeShow[F <: CronField]: Show[DivisibleNode[F]] =
    Show.fromToString[DivisibleNode[F]]

  implicit def divisibleNodeInstance[F <: CronField]: FieldExpr[DivisibleNode, F] =
    new FieldExpr[DivisibleNode, F] {
      def matches(node: DivisibleNode[F]): Predicate[Int] =
        _root_.cron4s.expr.ops.matches(node.raw)

      def implies[EE[_ <: CronField]](
          node: DivisibleNode[F]
      )(ee: EE[F])(implicit EE: FieldExpr[EE, F]): Boolean =
        node.raw match {
          case each: EachNode[F]       => each.implies(ee)
          case between: BetweenNode[F] => between.implies(ee)
          case several: SeveralNode[F] => several.implies(ee)
          case _                       => sys.error("Impossible!")
        }

      def range(node: DivisibleNode[F]): IndexedSeq[Int] =
        _root_.cron4s.expr.ops.range(node.raw)

      def unit(node: DivisibleNode[F]): CronUnit[F] = _root_.cron4s.expr.ops.unit(node.raw)
    }
}
