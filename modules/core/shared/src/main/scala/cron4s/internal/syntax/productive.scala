package cron4s
package internal
package syntax

import cats.data.NonEmptyVector

import cron4s.internal.base.Productive

private[syntax] class ProductiveOps[A, X](self: A, tc: Productive[A, X]) {
  def unfold: NonEmptyVector[X] = tc.unfold(self)
}

private[syntax] trait ProductiveSyntax {
  implicit def toProductiveOps[A, X](
      target: A
  )(implicit instance: Productive[A, X]): ProductiveOps[A, X] =
    new ProductiveOps[A, X](target, instance)
}

private[cron4s] object productive extends ProductiveSyntax
