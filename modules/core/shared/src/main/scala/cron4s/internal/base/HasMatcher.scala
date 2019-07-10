package cron4s
package internal
package base

import cats.instances.list._

import shapeless._

private[cron4s] trait HasMatcher[A] {
  type X

  def matches(a: A): Predicate[X]
}
private[cron4s] object HasMatcher extends HasMatcherDerivation {
  type Aux[A, X0] = HasMatcher[A] { type X = X0 }

  def apply[A, T](implicit ev: HasMatcher.Aux[A, T]): HasMatcher.Aux[A, T] = ev

  def instance[A, X0](f: A => Predicate[X0]): HasMatcher.Aux[A, X0] =
    new HasMatcher[A] {
      type X = X0
      def matches(a: A): Predicate[X] = f(a)
    }
}

private[base] trait HasMatcherDerivation extends HasMatcherDerivation1 {
  implicit def deriveHasMatcher[A, X, C <: Coproduct](
    implicit
    G: Generic.Aux[A, C],
    HM: HasMatcher.Aux[C, X]
  ): HasMatcher.Aux[A, X] =
    HasMatcher.instance(a => HM.matches(G.to(a)))
  
}

private[base] trait HasMatcherDerivation1 extends HasMatcherDerivation0 {

  implicit def deriveHasMatcherCoproduct[H, T <: Coproduct, X](
    implicit
    headHasMatcher: HasMatcher.Aux[H, X],
    tailHasMatcher: HasMatcher.Aux[T, X]
  ): HasMatcher.Aux[H :+: T, X] = 
    HasMatcher.instance { ht =>
      Predicate.anyOf(ht.head.map(headHasMatcher.matches).toList ++ ht.tail.map(tailHasMatcher.matches).toList)
    }
  
}

private[base] trait HasMatcherDerivation0 {
  
  implicit def deriveHasMatcherCNil[X]: HasMatcher.Aux[CNil, X] =
    HasMatcher.instance(_ => ???)
  
}