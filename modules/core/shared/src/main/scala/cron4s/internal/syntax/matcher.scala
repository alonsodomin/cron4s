package cron4s
package internal
package syntax

import cron4s.internal.base.{HasMatcher, Predicate}

private[syntax] class HasMatcherOps[A, T](self: A, tc: HasMatcher[A, T]) {
  def matches: Predicate[T] = tc.matches(self)
}

private[syntax] trait HasMatcherSyntax {
  implicit def toHasMatcherOps[A, T](target: A)(implicit A: HasMatcher[A, T]): HasMatcherOps[A, T] =
    new HasMatcherOps[A, T](target, A)
}

private[cron4s] object matcher extends HasMatcherSyntax