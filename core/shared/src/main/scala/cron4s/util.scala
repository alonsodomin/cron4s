package cron4s

import cron4s.expr._

import shapeless._

/**
  * Created by alonsodomin on 17/12/2016.
  */
private[cron4s] object util {

  object extract extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachExpr[F]](identity)
    implicit def caseConst[F <: CronField]   = at[ConstExpr[F]](identity)
    implicit def caseBetween[F <: CronField] = at[BetweenExpr[F]](identity)
    implicit def caseSeveral[F <: CronField] = at[SeveralExpr[F]](identity)
    implicit def caseEvery[F <: CronField]   = at[EveryExpr[F]](identity)
  }

  object matches extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachExpr[F]](_.matches)
    implicit def caseConst[F <: CronField]   = at[ConstExpr[F]](_.matches)
    implicit def caseBetween[F <: CronField] = at[BetweenExpr[F]](_.matches)
    implicit def caseSeveral[F <: CronField] = at[SeveralExpr[F]](_.matches)
    implicit def caseEvery[F <: CronField]   = at[EveryExpr[F]](_.matches)
    implicit def default[F <: CronField]     = at[FieldExpr[F]](_.fold(matches))
  }

  object range extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachExpr[F]](_.range)
    implicit def caseConst[F <: CronField]   = at[ConstExpr[F]](_.range)
    implicit def caseBetween[F <: CronField] = at[BetweenExpr[F]](_.range)
    implicit def caseSeveral[F <: CronField] = at[SeveralExpr[F]](_.range)
    implicit def caseEvery[F <: CronField]   = at[EveryExpr[F]](_.range)
    implicit def default[F <: CronField]     = at[FieldExpr[F]](_.fold(range))
  }

  object show extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachExpr[F]](_.toString)
    implicit def caseConst[F <: CronField]   = at[ConstExpr[F]](_.toString)
    implicit def caseBetween[F <: CronField] = at[BetweenExpr[F]](_.toString)
    implicit def caseSeveral[F <: CronField] = at[SeveralExpr[F]](_.toString)
    implicit def caseEvery[F <: CronField]   = at[EveryExpr[F]](_.toString)
    implicit def default[F <: CronField]     = at[FieldExpr[F]](_.fold(show))
  }

  object unit extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachExpr[F]](_.unit)
    implicit def caseConst[F <: CronField]   = at[ConstExpr[F]](_.unit)
    implicit def caseBetween[F <: CronField] = at[BetweenExpr[F]](_.unit)
    implicit def caseSeveral[F <: CronField] = at[SeveralExpr[F]](_.unit)
    implicit def caseEvery[F <: CronField]   = at[EveryExpr[F]](_.unit)
    implicit def default[F <: CronField]     = at[FieldExpr[F]](_.fold(unit))
  }

  object unify extends Poly1 {
    implicit def default[F <: CronField] = at[FieldExpr[F]](_.unify)
  }

}
