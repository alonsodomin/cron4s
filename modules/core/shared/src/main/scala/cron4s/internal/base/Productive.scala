package cron4s
package internal
package base

import cats.data.NonEmptyVector

import shapeless._

private[cron4s] trait Productive[T, E] {
  def unfold(t: T): NonEmptyVector[E]
}

private[cron4s] object Productive extends ProductiveDerivation {
  def apply[T, E](implicit ev: Productive[T, E]): Productive[T, E] = ev

  def instance[T, E](f: T => NonEmptyVector[E]): Productive[T, E] = new Productive[T, E] {
    def unfold(t: T): NonEmptyVector[E] = f(t)
  }
}

private[base] trait ProductiveDerivation extends ProductiveDerivation1 {

  implicit def deriveProductive[A, X, C <: Coproduct](
    implicit
    G: Generic.Aux[A, C],
    P: Productive[C, X]
  ): Productive[A, X] = new Productive[A, X] {
    def unfold(a: A): NonEmptyVector[X] =
      P.unfold(G.to(a))
  }

}

private[base] trait ProductiveDerivation1 extends ProductiveDerivation0 {

  implicit def deriveProductiveCoproduct[H, T <: Coproduct, X](
    implicit
    productiveH: Productive[H, X],
    productiveT: Productive[T, X]
  ): Productive[H :+: T, X] = new Productive[H :+: T, X] {
    def unfold(t: H :+: T): NonEmptyVector[X] = {
      t.head match {
        case Some(h) => productiveH.unfold(h)
        case None    => t.tail.map(productiveT.unfold).get
      }
    }
  }

}

private[base] trait ProductiveDerivation0 {

  implicit def deriveProductiveCNil[X]: Productive[CNil, X] =
    new Productive[CNil, X] {
      def unfold(t: CNil): NonEmptyVector[X] = ???
    }

}
