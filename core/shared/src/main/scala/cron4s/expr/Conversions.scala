package cron4s.expr

import cron4s.CronField

import shapeless.Coproduct

import scala.language.implicitConversions

/**
  * Created by alonsodomin on 28/12/2016.
  */
private[cron4s] trait Conversions {
  implicit def genericFieldCoprod[F <: CronField](expr: Node[F]): FieldNode[F] = expr match {
    case e: EachNode[F]    => Coproduct[FieldNode[F]](e)
    case e: ConstNode[F]   => Coproduct[FieldNode[F]](e)
    case e: BetweenNode[F] => Coproduct[FieldNode[F]](e)
    case e: SeveralNode[F] => Coproduct[FieldNode[F]](e)
    case e: EveryNode[F]   => Coproduct[FieldNode[F]](e)
  }

  implicit def const2Enumerable[F <: CronField](expr: ConstNode[F]): EnumerableNode[F] =
    Coproduct[EnumerableNode[F]](expr)

  implicit def between2Enumerable[F <: CronField](expr: BetweenNode[F]): EnumerableNode[F] =
    Coproduct[EnumerableNode[F]](expr)

  implicit def each2Divisible[F <: CronField](expr: EachNode[F]): DivisibleNode[F] =
    Coproduct[DivisibleNode[F]](expr)

  implicit def between2Divisible[F <: CronField](expr: BetweenNode[F]): DivisibleNode[F] =
    Coproduct[DivisibleNode[F]](expr)

  implicit def several2Divisible[F <: CronField](expr: SeveralNode[F]): DivisibleNode[F] =
    Coproduct[DivisibleNode[F]](expr)
}
