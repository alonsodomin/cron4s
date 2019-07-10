package cron4s
package syntax

import cats.Order

import cron4s.base.CircularTraverse

private[syntax] class CircularTraverseOps[F[_], A](self: F[A], tc: CircularTraverse[F]) {

  def step(from: A, stepSize: Int)(implicit A: Order[A]): (A, Int) = tc.step(self)(from, stepSize)

  def next(from: A)(implicit A: Order[A]): A = tc.next(self)(from)
  def prev(from: A)(implicit A: Order[A]): A = tc.prev(self)(from)

  def narrowBounds(lower: A, upper: A)(implicit A: Order[A]): F[A] =
    tc.narrowBounds(self)(lower, upper)

}

private[syntax] trait CircularTraverseSyntax {
  implicit def toCircularTraverseOps[F[_], A](
      target: F[A]
  )(implicit instance: CircularTraverse[F]): CircularTraverseOps[F, A] =
    new CircularTraverseOps[F, A](target, instance)
}

object circularTraverse extends CircularTraverseSyntax
