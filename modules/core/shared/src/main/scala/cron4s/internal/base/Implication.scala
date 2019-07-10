package cron4s
package base

trait Implication[A] {
  def implies[B, E](a: A)(other: B)(
    implicit
    hasMatcherA: HasMatcher[A, E],
    hasMatcherB: HasMatcher[B, E],
    productiveA: Productive[A, E],
    productiveB: Productive[B, E]
  ): Boolean

  def impliedBy[B, E](a: A)(other: B)(
    implicit
    hasMatcherA: HasMatcher[A, E],
    hasMatcherB: HasMatcher[B, E],
    productiveA: Productive[A, E],
    productiveB: Productive[B, E],
    implicationB: Implication[B]
  ): Boolean =
    implicationB.implies(other)(a)
  // def impliedBy[B](a: A)(other: B)(B: Implication[B]): Boolean =
  //   B.implies(other)(a)
}

object Implication {
  // def apply[A](implicit ev: Implication[A]): Implication[A] = ev
}