package cron4s.expr

import cron4s.CronField

import shapeless.Coproduct

import scala.language.implicitConversions

/**
  * Created by alonsodomin on 28/12/2016.
  */
private[cron4s] trait Conversions {
  implicit def exprToFieldCoprod[F <: CronField](expr: Node[F]): FieldNode[F] = expr match {
    case e: EachNode[F]    => Coproduct[FieldNode[F]](e)
    case e: ConstNode[F]   => Coproduct[FieldNode[F]](e)
    case e: BetweenNode[F] => Coproduct[FieldNode[F]](e)
    case e: SeveralNode[F] => Coproduct[FieldNode[F]](e)
    case e: EveryNode[F]   => Coproduct[FieldNode[F]](e)
  }

  implicit def constExpr2EnumCoprod[F <: CronField](expr: ConstNode[F]): SeveralMemberNode[F] =
    Coproduct[SeveralMemberNode[F]](expr)

  implicit def betweenExpr2EnumCoprod[F <: CronField](expr: BetweenNode[F]): SeveralMemberNode[F] =
    Coproduct[SeveralMemberNode[F]](expr)

  implicit def eachExpr2DivCoprod[F <: CronField](expr: EachNode[F]): FrequencyBaseNode[F] =
    Coproduct[FrequencyBaseNode[F]](expr)

  implicit def constExpr2DivCoprod[F <: CronField](expr: ConstNode[F]): FrequencyBaseNode[F] =
    Coproduct[FrequencyBaseNode[F]](expr)

  implicit def betweenExpr2DrivCoprod[F <: CronField](expr: BetweenNode[F]): FrequencyBaseNode[F] =
    Coproduct[FrequencyBaseNode[F]](expr)

  implicit def severalExpr2DivCoprod[F <: CronField](expr: SeveralNode[F]): FrequencyBaseNode[F] =
    Coproduct[FrequencyBaseNode[F]](expr)
}
