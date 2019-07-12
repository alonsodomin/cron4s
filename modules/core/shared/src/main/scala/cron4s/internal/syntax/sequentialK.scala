package cron4s
package internal
package syntax

import cats.Order

import cron4s.internal.base.SequentialK

private[syntax] class SequentialKOps[F[_], A](self: F[A], tc: SequentialK[F]) {

  def step(from: A, stepSize: Int)(implicit A: Order[A]): (A, Int) = tc.step(self)(from, stepSize)

  def next(from: A)(implicit A: Order[A]): A = tc.next(self)(from)
  def prev(from: A)(implicit A: Order[A]): A = tc.prev(self)(from)

  def narrowBounds(lower: A, upper: A)(implicit A: Order[A]): F[A] =
    tc.narrowBounds(self)(lower, upper)

}

private[syntax] trait SequentialKSyntax {
  implicit def toSequentialKOps[F[_], A](
      target: F[A]
  )(implicit instance: SequentialK[F]): SequentialKOps[F, A] =
    new SequentialKOps[F, A](target, instance)
}

private[cron4s] object sequentialK extends SequentialKSyntax
