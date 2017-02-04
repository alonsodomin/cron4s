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
import cron4s.base.Predicate

import shapeless._

import scalaz.Show

/**
  * Created by alonsodomin on 23/01/2017.
  */
final class FieldNode[F <: CronField](private[cron4s] val raw: RawFieldNode[F]) extends AnyVal {

  override def toString: String = raw.fold(ops.show)

}

object FieldNode {

  implicit def fieldNodeShow[F <: CronField]: Show[FieldNode[F]] =
    Show.shows(_.raw.fold(ops.show))

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

final class EnumerableNode[F <: CronField](val raw: RawEnumerableNode[F]) extends AnyVal {

  override def toString: String = raw.fold(ops.show)

}

object EnumerableNode {

  implicit def enumerableNodeShow[F <: CronField]: Show[EnumerableNode[F]] =
    Show.shows(_.raw.fold(ops.show))

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

final class DivisibleNode[F <: CronField](val raw: RawDivisibleNode[F]) extends AnyVal {

  override def toString: String = raw.fold(ops.show)

}

object DivisibleNode {

  implicit def divisibleNodeShow[F <: CronField]: Show[DivisibleNode[F]] =
    Show.shows(_.raw.fold(ops.show))

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
