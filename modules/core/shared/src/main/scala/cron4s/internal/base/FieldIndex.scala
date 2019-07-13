package cron4s
package internal
package base

import shapeless._

private[cron4s] trait FieldIndexed[A, F <: CronField] {
  type Out[x]

  def cast(a: A): Out[F]
}

private[cron4s] object FieldIndexed extends FieldIndexedInstances1 {
  type Aux[A, F <: CronField, O[_]] = FieldIndexed[A, F] { type Out[x] = O[x] }

  def apply[A, F <: CronField](implicit ev: FieldIndexed[A, F]): FieldIndexed[A, F] = ev

  implicit def mkFieldIndexedRefl[I[_], F <: CronField] = new FieldIndexed[I[F], F] {
    type Out[x] = I[x]

    @inline def cast(a: I[F]): Out[F] = a
  }
}

private[base] trait FieldIndexedInstances1 extends FieldIndexedInstances0 {
  implicit def coproductIsIndexed[H, T <: Coproduct, F <: CronField, O[_]](
      implicit
      indexedH: FieldIndexed.Aux[H, F, O],
      indexedT: FieldIndexed.Aux[T, F, O]
  ): FieldIndexed[H :+: T, F] = new FieldIndexed[H :+: T, F] {
    type Out[x] = O[x]
    def cast(ht: H :+: T): O[F] =
      ht.head match {
        case Some(h) => indexedH.cast(h)
        case None    => ht.tail.map(indexedT.cast).get
      }
  }
}

private[base] trait FieldIndexedInstances0 {
  implicit def cnilIsIndexed[F <: CronField]: FieldIndexed[CNil, F] = new FieldIndexed[CNil, F] {
    type Out[x] = Nothing
    def cast(a: CNil): Nothing = a.impossible
  }
}
