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
