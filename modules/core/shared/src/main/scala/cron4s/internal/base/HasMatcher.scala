package cron4s
package internal
package base

import cats.instances.list._

import shapeless._

private[cron4s] trait HasMatcher[A, X] {
  def matches(a: A): Predicate[X]
}
private[cron4s] object HasMatcher extends HasMatcherDerivation {
  
  def apply[A, X](implicit ev: HasMatcher[A, X]): HasMatcher[A, X] = ev

  def instance[A, X](f: A => Predicate[X]): HasMatcher[A, X] =
    new HasMatcher[A, X] {
      def matches(a: A): Predicate[X] = f(a)
    }
}

private[base] trait HasMatcherDerivation extends HasMatcherDerivation1 {
  implicit def deriveHasMatcher[A, X, C <: Coproduct](
    implicit
    G: Generic.Aux[A, C],
    HM: HasMatcher[C, X]
  ): HasMatcher[A, X] =
    HasMatcher.instance(a => HM.matches(G.to(a)))
  
}

private[base] trait HasMatcherDerivation1 extends HasMatcherDerivation0 {

  implicit def deriveHasMatcherCoproduct[H, T <: Coproduct, X](
    implicit
    headHasMatcher: HasMatcher[H, X],
    tailHasMatcher: HasMatcher[T, X]
  ): HasMatcher[H :+: T, X] = 
    HasMatcher.instance { ht =>
      Predicate.anyOf(ht.head.map(headHasMatcher.matches).toList ++ ht.tail.map(tailHasMatcher.matches).toList)
    }
  
}

private[base] trait HasMatcherDerivation0 {
  
  implicit def deriveHasMatcherCNil[X]: HasMatcher[CNil, X] =
    HasMatcher.instance(_.impossible)
  
}