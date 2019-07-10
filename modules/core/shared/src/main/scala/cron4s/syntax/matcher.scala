package cron4s
package syntax

import cron4s.base.{HasMatcher, Predicate}

private[syntax] class HasMatcherOps[A, T](self: A, tc: HasMatcher[A, T]) {
  def matches: Predicate[T] = tc.matches(self)
}

private[syntax] trait HasMatcherSyntax {
  implicit def toHasMatcherOps[A, T](target: A)(implicit A: HasMatcher[A, T]): HasMatcherOps[A, T] =
    new HasMatcherOps[A, T](target, A)
}

object matcher extends HasMatcherSyntax