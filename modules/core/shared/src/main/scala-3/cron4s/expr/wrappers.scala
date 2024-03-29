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

import cats.syntax.all._
import cron4s.CronField

/**
  * Created by alonsodomin on 23/01/2017.
  */
final class FieldNode[F <: CronField](private[cron4s] val raw: RawFieldNode[F]) extends AnyVal {
  override def toString: String = _root_.cron4s.expr.ops.show(raw)
}

object FieldNode extends FieldNodeInstances

final class FieldNodeWithAny[F <: CronField](private[cron4s] val raw: RawFieldNodeWithAny[F])
    extends AnyVal {
  override def toString: String = _root_.cron4s.expr.ops.show(raw)
}

object FieldNodeWithAny extends FieldNodeWithAnyInstances

final class EnumerableNode[F <: CronField](private[cron4s] val raw: RawEnumerableNode[F])
    extends AnyVal {
  override def toString: String = _root_.cron4s.expr.ops.show(raw)
}

object EnumerableNode extends EnumerableNodeInstances

final class DivisibleNode[F <: CronField](private[cron4s] val raw: RawDivisibleNode[F])
    extends AnyVal {
  override def toString: String = _root_.cron4s.expr.ops.show(raw)
}
object DivisibleNode extends DivisibleNodeInstances
