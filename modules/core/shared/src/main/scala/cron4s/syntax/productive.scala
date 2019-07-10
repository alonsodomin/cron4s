package cron4s
package syntax

import cats.data.NonEmptyVector

import cron4s.base.Productive

private[syntax] class ProductiveOps[T, E](self: T, tc: Productive[T, E]) {
  def unfold: NonEmptyVector[E] = tc.unfold(self)
}

private[syntax] trait ProductiveSyntax {
  implicit def toProductiveOps[T, E](
      target: T
  )(implicit instance: Productive[T, E]): ProductiveOps[T, E] =
    new ProductiveOps[T, E](target, instance)
}

object productive extends ProductiveSyntax
