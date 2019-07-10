package cron4s
package internal
package base

import shapeless._

private[cron4s] trait HasCronUnit[T, F <: CronField] {
  def unit(t: T): CronUnit[F]
}

private[cron4s] object HasCronUnit extends HasCronUnitDerivation {
  def apply[T, F <: CronField](implicit ev: HasCronUnit[T, F]): HasCronUnit[T, F] = ev

  def instance[T, F <: CronField](f: T => CronUnit[F]): HasCronUnit[T, F] =
    new HasCronUnit[T, F] {
      def unit(t: T): CronUnit[F] = f(t)
    }
}

private[base] trait HasCronUnitDerivation extends HasCronUnitDerivation1 {

  implicit def deriveHasCronUnit[T, F <: CronField, C <: Coproduct](
    implicit
    G: Generic.Aux[T, C],
    unitC: HasCronUnit[C, F]
  ): HasCronUnit[T, F] = new HasCronUnit[T, F] {
    def unit(t: T): CronUnit[F] = unitC.unit(G.to(t))
  }

}

private[base] trait HasCronUnitDerivation1 extends HasCronUnitDerivation0 {

  implicit def deriveHasCronUnitCoproduct[H, T <: Coproduct, F <: CronField](
    implicit
    unitH: HasCronUnit[H, F],
    unitT: HasCronUnit[T, F]
  ): HasCronUnit[H :+: T, F] = new HasCronUnit[H :+: T, F] {
    def unit(x: H :+: T): CronUnit[F] = {
      x.head match {
        case Some(h) => unitH.unit(h)
        case None    => x.tail.map(unitT.unit).get
      }
    }
  }

}

private[base] trait HasCronUnitDerivation0 {

  implicit def deriveHasCronUnit[F <: CronField]: HasCronUnit[CNil, F] =
    new HasCronUnit[CNil, F] {
      def unit(t: CNil): CronUnit[F] = ???
    }

}