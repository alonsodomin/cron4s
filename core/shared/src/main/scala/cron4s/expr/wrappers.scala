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

import cron4s.{CronField, CronUnit}
import cron4s.base.Predicate

import shapeless._

/**
  * Created by alonsodomin on 23/01/2017.
  */
final class FieldNode[F <: CronField](private[cron4s] val raw: RawFieldNode[F]) extends AnyVal {

  override def toString: String = raw.fold(ops.show)

}

object FieldNode {

  implicit def fieldNodeEq[F <: CronField]: Eq[FieldNode[F]] = Eq.fromUniversalEquals

  implicit def fieldNodeShow[F <: CronField]: Show[FieldNode[F]] =
    Show.fromToString[FieldNode[F]]

  implicit def fieldNodeInstance[F <: CronField]: FieldExpr[FieldNode, F] = new FieldExpr[FieldNode, F] {
    def matches(node: FieldNode[F]): Predicate[Int] =
      node.raw.fold(ops.matches)

    def range(node: FieldNode[F]): IndexedSeq[Int] =
      node.raw.fold(ops.range)

    def implies[EE[_ <: CronField]](node: FieldNode[F])(ee: EE[F])
      (implicit EE: FieldExpr[EE, F]): Boolean = node.raw match {
        case Inl(each)                      => each.implies(ee)
        case Inr(Inl(const))                => const.implies(ee)
        case Inr(Inr(Inl(between)))         => between.implies(ee)
        case Inr(Inr(Inr(Inl(several))))    => several.implies(ee)
        case Inr(Inr(Inr(Inr(Inl(every))))) => every.implies(ee)
        case _                              => sys.error("Impossible!")
      }

    def unit(node: FieldNode[F]): CronUnit[F] =
      node.raw.fold(ops.unit)
  }

}

final class FieldNodeWithAny[F <: CronField](private[cron4s] val raw: RawFieldNodeWithAny[F]) extends AnyVal {

  override def toString: String = raw.fold(ops.show)

}

object FieldNodeWithAny {

  implicit def fieldNodeWithAnyEq[F <: CronField]: Eq[FieldNodeWithAny[F]] = Eq.fromUniversalEquals

  implicit def fieldNodeWithAnyShow[F <: CronField]: Show[FieldNodeWithAny[F]] =
    Show.fromToString[FieldNodeWithAny[F]]

  implicit def fieldNodeInstance[F <: CronField]: FieldExpr[FieldNodeWithAny, F] =
    new FieldExpr[FieldNodeWithAny, F] {
      def matches(node: FieldNodeWithAny[F]): Predicate[Int] =
        node.raw.fold(ops.matches)

      def range(node: FieldNodeWithAny[F]): IndexedSeq[Int] =
        node.raw.fold(ops.range)

      def implies[EE[_ <: CronField]](node: FieldNodeWithAny[F])(ee: EE[F])
        (implicit EE: FieldExpr[EE, F]): Boolean = node.raw match {
          case Inl(any)  => any.implies(ee)
          case Inr(tail) => new FieldNode[F](tail).implies(ee)
        }

      def unit(node: FieldNodeWithAny[F]): CronUnit[F] =
        node.raw.fold(ops.unit)
    }

}

final class EnumerableNode[F <: CronField](private[cron4s] val raw: RawEnumerableNode[F]) extends AnyVal {

  override def toString: String = raw.fold(ops.show)

}

object EnumerableNode {

  implicit def enumerableNodeEq[F <: CronField]: Eq[EnumerableNode[F]] = Eq.fromUniversalEquals

  implicit def enumerableNodeShow[F <: CronField]: Show[EnumerableNode[F]] =
    Show.fromToString[EnumerableNode[F]]

  implicit def enumerableNodeInstance[F <: CronField]: FieldExpr[EnumerableNode, F] =
    new FieldExpr[EnumerableNode, F] {
      def matches(node: EnumerableNode[F]): Predicate[Int] =
        node.raw.fold(ops.matches)

      def implies[EE[_ <: CronField]](node: EnumerableNode[F])(ee: EE[F])
        (implicit EE: FieldExpr[EE, F]): Boolean = {
          node.raw match {
            case Inl(const)        => const.implies(ee)
            case Inr(Inl(between)) => between.implies(ee)
            case _                 => sys.error("Impossible!")
          }
        }

      def range(node: EnumerableNode[F]): IndexedSeq[Int] =
        node.raw.fold(ops.range)

      def unit(node: EnumerableNode[F]): CronUnit[F] =
        node.raw.fold(ops.unit)
    }

}

final class DivisibleNode[F <: CronField](private[cron4s] val raw: RawDivisibleNode[F]) extends AnyVal {

  override def toString: String = raw.fold(ops.show)

}

object DivisibleNode {

  implicit def divisibleNodeEq[F <: CronField]: Eq[DivisibleNode[F]] = Eq.fromUniversalEquals

  implicit def divisibleNodeShow[F <: CronField]: Show[DivisibleNode[F]] =
    Show.fromToString[DivisibleNode[F]]

  implicit def divisibleNodeInstance[F <: CronField]: FieldExpr[DivisibleNode, F] =
    new FieldExpr[DivisibleNode, F] {
      def matches(node: DivisibleNode[F]): Predicate[Int] =
        node.raw.fold(ops.matches)

      def implies[EE[_ <: CronField]](node: DivisibleNode[F])(ee: EE[F])
        (implicit EE: FieldExpr[EE, F]): Boolean = node.raw match {
          case Inl(each)              => each.implies(ee)
          case Inr(Inl(between))      => between.implies(ee)
          case Inr(Inr(Inl(several))) => several.implies(ee)
          case _                      => sys.error("Impossible!")
        }

      def range(node: DivisibleNode[F]): IndexedSeq[Int] =
        node.raw.fold(ops.range)

      def unit(node: DivisibleNode[F]): CronUnit[F] =
        node.raw.fold(ops.unit)
    }

}
