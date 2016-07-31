package cron4s.expr

import cron4s.{BaseGenerators, IsCronUnit}
import cron4s.expr.Expr.{BetweenExpr, ConstExpr}
import org.scalacheck._

/**
  * Created by alonsodomin on 31/07/2016.
  */
trait ExprGenerators extends BaseGenerators {

  private[this] def createConst[A](unit: A, value: Int)(implicit isUnit: IsCronUnit[A]): ConstExpr[isUnit.F] =
    ConstExpr[isUnit.F](isUnit(unit).field, value)(isUnit(unit))

  private[this] def createBetween[A](unit: A, min: Int, max: Int)(implicit isUnit: IsCronUnit[A]): BetweenExpr[isUnit.F] =
    BetweenExpr[isUnit.F](createConst(isUnit(unit), min), createConst(isUnit(unit), max))(isUnit(unit))

  lazy val constExpressions = for {
    unit  <- cronUnits
    value <- Gen.choose(unit.min, unit.max)
  } yield createConst(unit, value)

  lazy val betweenExpressions = for {
    unit <- cronUnits
    min  <- Gen.choose(unit.min, unit.max - 1)
    max  <- Gen.choose(min + 1, unit.max)
  } yield createBetween(unit, min, max)

}
