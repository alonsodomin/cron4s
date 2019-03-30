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
package syntax

import cron4s.base.{Step, Steppable}

private[syntax] final class SteppableOps[T, E](self: T, tc: Steppable[T, E]) {
  protected[cron4s] def step(from: E, step: Step): Either[StepError, (E, Int)] =
    tc.step(self, from, step)

  def step(from: E, stepSize: Int): Either[StepError, (E, Int)] =
    tc.step(self)(from, stepSize)

  def next(from: E): Option[E] = tc.next(self)(from)
  def prev(from: E): Option[E] = tc.prev(self)(from)
}

private[syntax] trait SteppableSyntax {
  implicit def toSteppableOps[T, E](t: T)(implicit tc: Steppable[T, E]): SteppableOps[T, E] =
    new SteppableOps(t, tc)
}

object steppable extends SteppableSyntax
