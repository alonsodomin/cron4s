package cron4s

import cron4s.expr._
import cron4s.types.HasCronField
import cron4s.validation.ExprValidator
import shapeless._

/**
  * Created by alonsodomin on 17/12/2016.
  */
private[cron4s] object generic {

  object matches extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachExpr[F]](_.matches)
    implicit def caseConst[F <: CronField]   = at[ConstExpr[F]](_.matches)
    implicit def caseBetween[F <: CronField] = at[BetweenExpr[F]](_.matches)
    implicit def caseSeveral[F <: CronField] = at[SeveralExpr[F]](_.matches)
    implicit def caseEvery[F <: CronField]   = at[EveryExpr[F]](_.matches)

    implicit def defaultField[F <: CronField] = at[FieldExprAST[F]](_.fold(matches))
    implicit def defaultEnum[F <: CronField]  = at[EnumExprAST[F]](_.fold(matches))
    implicit def defaultDiv[F <: CronField]   = at[DivExprAST[F]](_.fold(matches))
  }

  object range extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachExpr[F]](_.range)
    implicit def caseConst[F <: CronField]   = at[ConstExpr[F]](_.range)
    implicit def caseBetween[F <: CronField] = at[BetweenExpr[F]](_.range)
    implicit def caseSeveral[F <: CronField] = at[SeveralExpr[F]](_.range)
    implicit def caseEvery[F <: CronField]   = at[EveryExpr[F]](_.range)
    implicit def default[F <: CronField]     = at[FieldExprAST[F]](_.fold(range))
  }

  object show extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachExpr[F]](_.toString)
    implicit def caseConst[F <: CronField]   = at[ConstExpr[F]](_.toString)
    implicit def caseBetween[F <: CronField] = at[BetweenExpr[F]](_.toString)
    implicit def caseSeveral[F <: CronField] = at[SeveralExpr[F]](_.toString)
    implicit def caseEvery[F <: CronField]   = at[EveryExpr[F]](_.toString)
    implicit def default[F <: CronField]     = at[FieldExprAST[F]](_.fold(show))
  }

  object unit extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachExpr[F]](_.unit)
    implicit def caseConst[F <: CronField]   = at[ConstExpr[F]](_.unit)
    implicit def caseBetween[F <: CronField] = at[BetweenExpr[F]](_.unit)
    implicit def caseSeveral[F <: CronField] = at[SeveralExpr[F]](_.unit)
    implicit def caseEvery[F <: CronField]   = at[EveryExpr[F]](_.unit)
    implicit def default[F <: CronField]     = at[FieldExprAST[F]](_.fold(unit))
  }

  object validate extends Poly1 {
    implicit def caseEach[F <: CronField](implicit validator: ExprValidator[EachExpr, F], ev: HasCronField[CronUnit, F]) = at[EachExpr[F]](validator.validate)
    implicit def caseConst[F <: CronField](implicit validator: ExprValidator[ConstExpr, F], ev: HasCronField[CronUnit, F]) = at[ConstExpr[F]](validator.validate)
    implicit def caseBetween[F <: CronField](implicit validator: ExprValidator[BetweenExpr, F], ev: HasCronField[CronUnit, F]) = at[BetweenExpr[F]](validator.validate)
    implicit def caseSeveral[F <: CronField](implicit validator: ExprValidator[SeveralExpr, F], ev: HasCronField[CronUnit, F]) = at[SeveralExpr[F]](validator.validate)
    implicit def caseEvery[F <: CronField](implicit validator: ExprValidator[EveryExpr, F], ev: HasCronField[CronUnit, F]) = at[EveryExpr[F]](validator.validate)
  }

}
