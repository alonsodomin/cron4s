package cron4s
package internal
package base

import cats.instances.list._

import shapeless._

private[cron4s] trait HasMatcher[A, T] {
  def matches(a: A): Predicate[T]
}
private[cron4s] object HasMatcher extends HasMatcherDerivation {
  def apply[A, T](implicit ev: HasMatcher[A, T]): HasMatcher[A, T] = ev

  def instance[A, T](f: A => Predicate[T]): HasMatcher[A, T] =
    new HasMatcher[A, T] {
      def matches(a: A): Predicate[T] = f(a)
    }
}

private[base] trait HasMatcherDerivation extends HasMatcherDerivation1 {
  implicit def deriveHasMatcher[A, X, C <: Coproduct](
    implicit
    G: Generic.Aux[A, C],
    HM: HasMatcher[C, X]
  ): HasMatcher[A, X] = new HasMatcher[A, X] {
    def matches(a: A): Predicate[X] = Predicate[X] { x =>
      HM.matches(G.to(a))(x)
    }
  }
}

private[base] trait HasMatcherDerivation1 extends HasMatcherDerivation0 {

  implicit def deriveHasMatcherCoproduct[H, T <: Coproduct, X](
    implicit
    headHasMatcher: HasMatcher[H, X],
    tailHasMatcher: HasMatcher[T, X]
  ): HasMatcher[H :+: T, X] = new HasMatcher[H :+: T, X] {
    def matches(a: H :+: T): Predicate[X] = Predicate[X] { x =>
      val items = a.head.map(headHasMatcher.matches).toList ++ a.tail.map(tailHasMatcher.matches).toList
      val pred = Predicate.anyOf(items)
      pred(x)
    }
  }
}

private[base] trait HasMatcherDerivation0 {
  implicit def deriveHasMatcherCNil[X]: HasMatcher[CNil, X] =
    new HasMatcher[CNil, X] {
      def matches(a: CNil): Predicate[X] = ???
    }
}