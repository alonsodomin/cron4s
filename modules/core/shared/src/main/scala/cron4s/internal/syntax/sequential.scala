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
package internal
package syntax

import cats.Order

import cron4s.internal.base.Sequential

private[syntax] class SequentialOps[F[_], A: Order](self: F[A], tc: Sequential[F]) {

  def step(from: A, stepSize: Int): (A, Int) = tc.step(self)(from, stepSize)

  def next(from: A): A = tc.next(self)(from)
  def prev(from: A): A = tc.prev(self)(from)

  def narrowBounds(lower: A, upper: A): F[A] =
    tc.narrowBounds(self)(lower, upper)

}

private[syntax] trait SequentialSyntax {
  implicit def toSequentialKOps[F[_], A: Order](
      target: F[A]
  )(implicit instance: Sequential[F]): SequentialOps[F, A] =
    new SequentialOps[F, A](target, instance)
}

private[cron4s] object sequential extends SequentialSyntax
