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
import cron4s.types.{Expr, Predicate}

import scalaz.Show

/**
  * Created by alonsodomin on 23/01/2017.
  */
final class FieldExpr[F <: CronField](private[cron4s] val raw: RawFieldExpr[F]) extends AnyVal {

  override def toString: String = Show[FieldExpr[F]].shows(this)

}

object FieldExpr {
  implicit def fieldNodeInstance[F <: CronField]: Expr[FieldExpr, F] = new Expr[FieldExpr, F] {
    def matches(node: FieldExpr[F]): Predicate[Int] =
      node.raw.fold(generic.ops.matches)

    def range(node: FieldExpr[F]): IndexedSeq[Int] =
      node.raw.fold(generic.ops.range)

    def unit(node: FieldExpr[F]): CronUnit[F] =
      node.raw.fold(generic.ops.unit)

    override def shows(node: FieldExpr[F]): String =
      node.raw.fold(generic.ops.show)
  }
}

final class EnumerableExpr[F <: CronField](val raw: RawEnumerableExpr[F]) extends AnyVal

object EnumerableExpr {

  implicit def enumerableNodeInstance[F <: CronField]: Expr[EnumerableExpr, F] =
    new Expr[EnumerableExpr, F] {
      def matches(node: EnumerableExpr[F]): Predicate[Int] =
        node.raw.fold(generic.ops.matches)

      def range(node: EnumerableExpr[F]): IndexedSeq[Int] =
        node.raw.fold(generic.ops.range)

      def unit(node: EnumerableExpr[F]): CronUnit[F] =
        node.raw.fold(generic.ops.unit)

      override def shows(node: EnumerableExpr[F]): String =
        node.raw.fold(generic.ops.show)
    }

}

final class DivisibleExpr[F <: CronField](val raw: RawDivisibleExpr[F]) extends AnyVal

object DivisibleExpr {
  implicit def divisibleNodeInstance[F <: CronField]: Expr[DivisibleExpr, F] =
    new Expr[DivisibleExpr, F] {
      def matches(node: DivisibleExpr[F]): Predicate[Int] =
        node.raw.fold(generic.ops.matches)

      def range(node: DivisibleExpr[F]): IndexedSeq[Int] =
        node.raw.fold(generic.ops.range)

      def unit(node: DivisibleExpr[F]): CronUnit[F] =
        node.raw.fold(generic.ops.unit)

      override def shows(node: DivisibleExpr[F]): String =
        node.raw.fold(generic.ops.show)
    }
}