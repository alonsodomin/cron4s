package cron4s
package internal
package base

import shapeless._

private[cron4s] trait HasCronUnit[T] {
  type F <: CronField

  def unit(t: T): CronUnit[F]
}

private[cron4s] object HasCronUnit extends HasCronUnitDerivation {
  type Aux[A, F0 <: CronField] = HasCronUnit[A] { type F = F0 }

  def apply[T, F <: CronField](implicit ev: HasCronUnit.Aux[T, F]): HasCronUnit.Aux[T, F] = ev

  def instance[T, F0 <: CronField](f: T => CronUnit[F0]): HasCronUnit.Aux[T, F0] =
    new HasCronUnit[T] {
      type F = F0

      def unit(t: T): CronUnit[F] = f(t)
    }
}

private[base] trait HasCronUnitDerivation extends HasCronUnitDerivation1 {

  implicit def deriveHasCronUnit[T, F <: CronField, C <: Coproduct](
    implicit
    G: Generic.Aux[T, C],
    unitC: HasCronUnit.Aux[C, F]
  ): HasCronUnit.Aux[T, F] =
    HasCronUnit.instance(a => unitC.unit(G.to(a)))

}

private[base] trait HasCronUnitDerivation1 extends HasCronUnitDerivation0 {

  implicit def deriveHasCronUnitCoproduct[H, T <: Coproduct, F <: CronField](
    implicit
    unitH: HasCronUnit.Aux[H, F],
    unitT: HasCronUnit.Aux[T, F]
  ): HasCronUnit.Aux[H :+: T, F] =
    HasCronUnit.instance { x =>
      x.head match {
        case Some(h) => unitH.unit(h)
        case None    => x.tail.map(unitT.unit).get
      }
    }
  
}

private[base] trait HasCronUnitDerivation0 {

  implicit def deriveHasCronUnit[F <: CronField]: HasCronUnit.Aux[CNil, F] =
    HasCronUnit.instance(_ => ???)

}