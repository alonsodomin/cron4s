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

  implicit def const2SeveralMember[F <: CronField](expr: ConstNode[F]): SeveralMemberNode[F] =
    Coproduct[SeveralMemberNode[F]](expr)

  implicit def between2SeveralMember[F <: CronField](expr: BetweenNode[F]): SeveralMemberNode[F] =
    Coproduct[SeveralMemberNode[F]](expr)

  implicit def each2FrequencyBase[F <: CronField](expr: EachNode[F]): FrequencyBaseNode[F] =
    Coproduct[FrequencyBaseNode[F]](expr)

  implicit def between2FrequencyBase[F <: CronField](expr: BetweenNode[F]): FrequencyBaseNode[F] =
    Coproduct[FrequencyBaseNode[F]](expr)

  implicit def several2FrequencyBase[F <: CronField](expr: SeveralNode[F]): FrequencyBaseNode[F] =
    Coproduct[FrequencyBaseNode[F]](expr)
}
