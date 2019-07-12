package cron4s
package internal
package syntax

import cats.Order

import cron4s.internal.base.Sequential

private[syntax] class SequentialOps[A: Order, X](self: A, tc: Sequential[A, X]) {

  def step(from: X, stepSize: Int): (X, Int) = tc.step(self)(from, stepSize)

  def next(from: X): X = tc.next(self)(from)
  def prev(from: X): X = tc.prev(self)(from)

  def narrowBounds(lower: X, upper: X): A =
    tc.narrowBounds(self)(lower, upper)

}

private[syntax] trait SequentialSyntax {
  implicit def toSequentialKOps[A: Order, X](
      target: A
  )(implicit instance: Sequential[A, X]): SequentialOps[A, X] =
    new SequentialOps[A, X](target, instance)
}

private[cron4s] object sequential extends SequentialSyntax
