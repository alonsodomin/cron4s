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
package base

import cats.Traverse

trait Steppable[T, E] {
  protected[cron4s] def step(t: T, from: E, step: Step): Either[ExprError, (E, Int)]

  final def step(t: T)(from: E, stepSize: Int): Either[ExprError, (E, Int)] =
    if (stepSize == Int.MinValue || stepSize == Int.MaxValue) Left(StepSizeOutOfRange(stepSize))
    else step(t, from, Step(stepSize))

  final def next(t: T)(from: E): Either[ExprError, E] = step(t)(from, 1).map(_._1)
  final def prev(t: T)(from: E): Either[ExprError, E] = step(t)(from, -1).map(_._1)
}
object Steppable {
  @inline def apply[T, E](implicit ev: Steppable[T, E]): Steppable[T, E] = ev
}
