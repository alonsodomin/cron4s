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

import cron4s.{CronField, CronUnit, generic}
import cron4s.types.Predicate

import scalaz.Show

/**
  * Created by alonsodomin on 23/01/2017.
  */
final class FieldNode[F <: CronField](private[cron4s] val raw: RawFieldNode[F]) extends AnyVal {

  override def toString: String = raw.fold(generic.ops.show)

}

object FieldNode {
  implicit def fieldNodeInstance[F <: CronField]: Expr[FieldNode, F] = new Expr[FieldNode, F] {
    def matches(node: FieldNode[F]): Predicate[Int] =
      node.raw.fold(generic.ops.matches)

    def range(node: FieldNode[F]): IndexedSeq[Int] =
      node.raw.fold(generic.ops.range)

    def unit(node: FieldNode[F]): CronUnit[F] =
      node.raw.fold(generic.ops.unit)

    override def shows(node: FieldNode[F]): String =
      node.raw.fold(generic.ops.show)
  }
}

final class EnumerableNode[F <: CronField](val raw: RawEnumerableNode[F]) extends AnyVal {

  override def toString: String = raw.fold(generic.ops.show)

}

object EnumerableNode {

  implicit def enumerableNodeInstance[F <: CronField]: Expr[EnumerableNode, F] =
    new Expr[EnumerableNode, F] {
      def matches(node: EnumerableNode[F]): Predicate[Int] =
        node.raw.fold(generic.ops.matches)

      def range(node: EnumerableNode[F]): IndexedSeq[Int] =
        node.raw.fold(generic.ops.range)

      def unit(node: EnumerableNode[F]): CronUnit[F] =
        node.raw.fold(generic.ops.unit)

      override def shows(node: EnumerableNode[F]): String =
        node.raw.fold(generic.ops.show)
    }

}

final class DivisibleNode[F <: CronField](val raw: RawDivisibleNode[F]) extends AnyVal {

  override def toString: String = raw.fold(generic.ops.show)

}

object DivisibleNode {
  implicit def divisibleNodeInstance[F <: CronField]: Expr[DivisibleNode, F] =
    new Expr[DivisibleNode, F] {
      def matches(node: DivisibleNode[F]): Predicate[Int] =
        node.raw.fold(generic.ops.matches)

      def range(node: DivisibleNode[F]): IndexedSeq[Int] =
        node.raw.fold(generic.ops.range)

      def unit(node: DivisibleNode[F]): CronUnit[F] =
        node.raw.fold(generic.ops.unit)

      override def shows(node: DivisibleNode[F]): String =
        node.raw.fold(generic.ops.show)
    }
}
