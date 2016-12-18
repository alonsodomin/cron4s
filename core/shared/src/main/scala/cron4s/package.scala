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

}
