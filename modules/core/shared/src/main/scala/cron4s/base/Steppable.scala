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

final case class Step private[cron4s] (amount: Int, direction: Direction) {
  require(amount >= 0, "Step amount must be a positive integer")

  def reverse: Step = copy(direction = direction.reverse)

}

object Step {
  def apply(stepSize: Int): Step =
    new Step(Math.abs(stepSize), Direction.ofSign(stepSize))
}

sealed abstract class Direction(private[cron4s] val sign: Int) {
  def reverse: Direction
}
object Direction {

  def ofSign(step: Int): Direction =
    if (step >= 0) Forward
    else Backwards

  case object Forward extends Direction(1) {
    def reverse: Direction = Backwards
  }
  case object Backwards extends Direction(-1) {
    def reverse: Direction = Forward
  }
}

trait Steppable[T, E] {
  protected[cron4s] def step(t: T, from: E, step: Step): Either[StepError, (E, Int)]

  final def step(t: T)(from: E, stepSize: Int): Either[StepError, (E, Int)] =
    if (stepSize == Int.MinValue || stepSize == Int.MaxValue) Left(StepSizeOutOfRange(stepSize))
    else step(t, from, Step(stepSize))

  final def next(t: T)(from: E): Option[E] = step(t)(from, 1).toOption.map(_._1)
  final def prev(t: T)(from: E): Option[E] = step(t)(from, -1).toOption.map(_._1)
}
object Steppable {
  @inline def apply[T, E](implicit ev: Steppable[T, E]): Steppable[T, E] = ev
}
