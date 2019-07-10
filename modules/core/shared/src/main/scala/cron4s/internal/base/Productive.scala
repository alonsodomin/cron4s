package cron4s
package internal
package base

import cats.data.NonEmptyVector

import shapeless._

private[cron4s] trait Productive[T] {
  type X
  def unfold(t: T): NonEmptyVector[X]
}

private[cron4s] object Productive extends ProductiveDerivation {
  type Aux[A, X0] = Productive[A] { type X = X0 }

  def apply[T, E](implicit ev: Productive.Aux[T, E]): Productive.Aux[T, E] = ev

  def instance[T, X0](f: T => NonEmptyVector[X0]): Productive.Aux[T, X0] = new Productive[T] {
    type X = X0
    def unfold(t: T): NonEmptyVector[X] = f(t)
  }
}

private[base] trait ProductiveDerivation extends ProductiveDerivation1 {

  implicit def deriveProductive[A, X, C <: Coproduct](
    implicit
    G: Generic.Aux[A, C],
    P: Productive.Aux[C, X]
  ): Productive.Aux[A, X] =
    Productive.instance(a => P.unfold(G.to(a)))

}

private[base] trait ProductiveDerivation1 extends ProductiveDerivation0 {

  implicit def deriveProductiveCoproduct[H, T <: Coproduct, X](
    implicit
    productiveH: Productive.Aux[H, X],
    productiveT: Productive.Aux[T, X]
  ): Productive.Aux[H :+: T, X] =
    Productive.instance { ht =>
      ht.head match {
        case Some(h) => productiveH.unfold(h)
        case None    => ht.tail.map(productiveT.unfold).get
      }
    }

}

private[base] trait ProductiveDerivation0 {

  implicit def deriveProductiveCNil[X]: Productive.Aux[CNil, X] =
    Productive.instance(_ => ???)

}
