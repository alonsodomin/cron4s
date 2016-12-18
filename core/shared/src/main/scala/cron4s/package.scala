import cron4s.syntax.AllSyntax
import cron4s.expr._

import shapeless.Coproduct

import scala.language.implicitConversions

package object cron4s extends AllSyntax {

  implicit def exprToFieldCoprod[F <: CronField](expr: Expr[F]): FieldExprAST[F] = expr match {
    case e: EachExpr[F]    => Coproduct[FieldExprAST[F]](e)
    case e: ConstExpr[F]   => Coproduct[FieldExprAST[F]](e)
    case e: BetweenExpr[F] => Coproduct[FieldExprAST[F]](e)
    case e: SeveralExpr[F] => Coproduct[FieldExprAST[F]](e)
    case e: EveryExpr[F]   => Coproduct[FieldExprAST[F]](e)
  }

  implicit def constExpr2EnumCoprod[F <: CronField](expr: ConstExpr[F]): EnumExprAST[F] =
    Coproduct[EnumExprAST[F]](expr)

  implicit def betweenExpr2EnumCoprod[F <: CronField](expr: BetweenExpr[F]): EnumExprAST[F] =
    Coproduct[EnumExprAST[F]](expr)

  implicit def eachExpr2DivCoprod[F <: CronField](expr: EachExpr[F]): DivExprAST[F] =
    Coproduct[DivExprAST[F]](expr)

  implicit def constExpr2DivCoprod[F <: CronField](expr: ConstExpr[F]): DivExprAST[F] =
    Coproduct[DivExprAST[F]](expr)

  implicit def betweenExpr2DrivCoprod[F <: CronField](expr: BetweenExpr[F]): DivExprAST[F] =
    Coproduct[DivExprAST[F]](expr)

  implicit def severalExpr2DivCoprod[F <: CronField](expr: SeveralExpr[F]): DivExprAST[F] =
    Coproduct[DivExprAST[F]](expr)

}
