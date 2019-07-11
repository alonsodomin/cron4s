package cron4s
package internal
package base

import cats.data.NonEmptyVector

import shapeless._

private[cron4s] trait Productive[A, X] {
  def unfold(a: A): NonEmptyVector[X]
}

private[cron4s] object Productive extends ProductiveDerivation {
  
  def apply[A, X](implicit ev: Productive[A, X]): Productive[A, X] = ev

  def instance[A, X](f: A => NonEmptyVector[X]): Productive[A, X] = new Productive[A, X] {
    def unfold(a: A): NonEmptyVector[X] = f(a)
  }
}

private[base] trait ProductiveDerivation extends ProductiveDerivation1 {

  implicit def deriveProductive[A, X, C <: Coproduct](
    implicit
    G: Generic.Aux[A, C],
    P: Productive[C, X]
  ): Productive[A, X] =
    Productive.instance(a => P.unfold(G.to(a)))

}

private[base] trait ProductiveDerivation1 extends ProductiveDerivation0 {

  implicit def deriveProductiveCoproduct[H, T <: Coproduct, X](
    implicit
    productiveH: Productive[H, X],
    productiveT: Productive[T, X]
  ): Productive[H :+: T, X] =
    Productive.instance { ht =>
      ht.head match {
        case Some(h) => productiveH.unfold(h)
        case None    => ht.tail.map(productiveT.unfold).get
      }
    }

}

private[base] trait ProductiveDerivation0 {

  implicit def deriveProductiveCNil[X]: Productive[CNil, X] =
    Productive.instance(_ => ???)

}
