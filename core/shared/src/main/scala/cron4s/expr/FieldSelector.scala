package cron4s.expr

import cron4s.CronField
import shapeless.Coproduct
import shapeless.ops.coproduct.Selector

/**
  * Created by alonsodomin on 10/02/2017.
  */
trait FieldSelector[F <: CronField] {
  type Out[X <: CronField]

  def select[CN <: Coproduct](expr: CN)(implicit select: Selector[CN, Out[F]]): Option[Out[F]] =
    select.apply(expr)
}
