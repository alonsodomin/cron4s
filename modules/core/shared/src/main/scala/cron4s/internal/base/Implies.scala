package cron4s
package internal
package base

import shapeless._

trait Implies[A, F <: CronField] {
  def implies[B](a: A)(b: B)(
      implicit
      indexedA: FieldIndexed[A, F],
      indexedB: FieldIndexed[B, F]
  ): Boolean

  def impliedBy[B](a: A)(b: B)(
      indexedA: FieldIndexed[A, F],
      indexedB: FieldIndexed[B, F],
      impliesB: Implies[B, F]
  ): Boolean = impliesB.implies(b)(a)(indexedB, indexedA)

}

object Implies extends ImpliesDerivation {
  def apply[A, F <: CronField](implicit ev: Implies[A, F]): Implies[A, F] = ev
}

private[base] trait ImpliesDerivation extends ImpliesDerivation1 {
  def derivesImplies[A, F <: CronField, C <: Coproduct](
      implicit
      G: Generic.Aux[A, C],
      I: Lazy[Implies[C, F]],
      X: FieldIndexed[C, F]
  ): Implies[A, F] = new Implies[A, F] {
    def implies[B](a: A)(b: B)(
        implicit
        indexedA: FieldIndexed[A, F],
        indexedB: FieldIndexed[B, F]
    ): Boolean =
      I.value.implies(G.to(a))(b)
  }
}

private[base] trait ImpliesDerivation1 extends ImpliesDerivation0 {

  implicit def deriveImpliesCoproduct[H, T <: Coproduct, F <: CronField](
      implicit
      impliesH: Implies[H, F],
      indexedH: FieldIndexed[H, F],
      impliesT: Implies[T, F],
      indexedT: FieldIndexed[T, F]
  ): Implies[H :+: T, F] = new Implies[H :+: T, F] {
    def implies[B](ht: H :+: T)(b: B)(
        implicit
        indexedHT: FieldIndexed[H :+: T, F],
        indexedB: FieldIndexed[B, F]
    ): Boolean =
      ht.head match {
        case Some(h) => impliesH.implies(h)(b)
        case None    => ht.tail.map(impliesT.implies(_)(b)).get
      }
  }

}

private[base] trait ImpliesDerivation0 {

  implicit def deriveImpliesCNil[F <: CronField]: Implies[CNil, F] =
    new Implies[CNil, F] {
      def implies[B](a: CNil)(b: B)(
          implicit
          indexedA: FieldIndexed[CNil, F],
          indexedB: FieldIndexed[B, F]
      ): Boolean = a.impossible
    }

}
