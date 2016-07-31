package cron4s.expr

import cron4s.{BaseGenerators, IsCronUnit}
import cron4s.expr.Expr.ConstExpr
import org.scalacheck._

/**
  * Created by alonsodomin on 31/07/2016.
  */
trait ExprGenerators extends BaseGenerators {

  private[this] def createConst[A](unit: A, value: Int)(implicit isUnit: IsCronUnit[A]): ConstExpr[isUnit.F] =
    ConstExpr[isUnit.F](isUnit(unit).field, value)(isUnit(unit))

  lazy val constExpressions = for {
    unit <- cronUnits
    value <- Gen.choose(unit.min, unit.max)
  } yield createConst(unit, value)

}
