package cron4s.generic

import cron4s.expr._
import cron4s.types.Enumerated
import cron4s.validation.NodeValidator
import cron4s.{CronField, CronUnit}

import shapeless._

/**
  * Created by alonsodomin on 17/12/2016.
  */
private[cron4s] object ops {

  object matches extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachNode[F]](_.matches)
    implicit def caseConst[F <: CronField]   = at[ConstNode[F]](_.matches)
    implicit def caseBetween[F <: CronField] = at[BetweenNode[F]](_.matches)
    implicit def caseSeveral[F <: CronField] = at[SeveralNode[F]](_.matches)
    implicit def caseEvery[F <: CronField]   = at[EveryNode[F]](_.matches)

    implicit def defaultF[F <: CronField]  = at[FieldNode[F]](_.fold(matches))
    implicit def defaultSM[F <: CronField] = at[SeveralMemberNode[F]](_.fold(matches))
    implicit def defaultFB[F <: CronField] = at[FrequencyBaseNode[F]](_.fold(matches))
  }

  object range extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachNode[F]](_.range)
    implicit def caseConst[F <: CronField]   = at[ConstNode[F]](_.range)
    implicit def caseBetween[F <: CronField] = at[BetweenNode[F]](_.range)
    implicit def caseSeveral[F <: CronField] = at[SeveralNode[F]](_.range)
    implicit def caseEvery[F <: CronField]   = at[EveryNode[F]](_.range)
    implicit def default[F <: CronField]     = at[FieldNode[F]](_.fold(range))
  }

  object show extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachNode[F]](_.toString)
    implicit def caseConst[F <: CronField]   = at[ConstNode[F]](_.toString)
    implicit def caseBetween[F <: CronField] = at[BetweenNode[F]](_.toString)
    implicit def caseSeveral[F <: CronField] = at[SeveralNode[F]](_.toString)
    implicit def caseEvery[F <: CronField]   = at[EveryNode[F]](_.toString)
    implicit def default[F <: CronField]     = at[FieldNode[F]](_.fold(show))
  }

  object unit extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachNode[F]](_.unit)
    implicit def caseConst[F <: CronField]   = at[ConstNode[F]](_.unit)
    implicit def caseBetween[F <: CronField] = at[BetweenNode[F]](_.unit)
    implicit def caseSeveral[F <: CronField] = at[SeveralNode[F]](_.unit)
    implicit def caseEvery[F <: CronField]   = at[EveryNode[F]](_.unit)
    implicit def default[F <: CronField]     = at[FieldNode[F]](_.fold(unit))
  }

  object validate extends Poly1 {
    implicit def caseEach[F <: CronField](
        implicit
        validator: NodeValidator[EachNode[F]],
        ev: Enumerated[CronUnit[F]]
      ) = at[EachNode[F]](validator.validate)

    implicit def caseConst[F <: CronField](
        implicit
        validator: NodeValidator[ConstNode[F]],
        ev: Enumerated[CronUnit[F]]
      ) = at[ConstNode[F]](validator.validate)

    implicit def caseBetween[F <: CronField](
        implicit
        validator: NodeValidator[BetweenNode[F]],
        ev: Enumerated[CronUnit[F]]
      ) = at[BetweenNode[F]](validator.validate)

    implicit def caseSeveral[F <: CronField](
        implicit
        validator: NodeValidator[SeveralNode[F]],
        ev: Enumerated[CronUnit[F]]
     ) = at[SeveralNode[F]](validator.validate)

    implicit def caseEvery[F <: CronField](
        implicit
        validator: NodeValidator[EveryNode[F]],
        ev: Enumerated[CronUnit[F]]
      ) = at[EveryNode[F]](validator.validate)

    implicit def defaultField[F <: CronField](
        implicit
        validator: NodeValidator[FieldNode[F]],
        ev: Enumerated[CronUnit[F]]
      ) = at[FieldNode[F]](validator.validate)
  }

}
