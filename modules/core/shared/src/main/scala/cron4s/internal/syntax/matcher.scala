package cron4s
package internal
package syntax

import cron4s.internal.base.{HasMatcher, Predicate}

private[syntax] class HasMatcherOps[A, X](self: A, tc: HasMatcher[A, X]) {
  def matches: Predicate[X] = tc.matches(self)
}

private[syntax] trait HasMatcherSyntax {
  implicit def toHasMatcherOps[A, X](target: A)(implicit A: HasMatcher[A, X]): HasMatcherOps[A, X] =
    new HasMatcherOps[A, X](target, A)
}

private[cron4s] object matcher extends HasMatcherSyntax