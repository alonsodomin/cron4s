import cron4s.syntax.AllSyntax
import cron4s.expr._

import shapeless.Coproduct

import scala.language.implicitConversions

package object cron4s extends AllSyntax {

  implicit def exprToFieldCoprod[F <: CronField](expr: Expr[F]): FieldExpr[F] = expr match {
    case e: EachExpr[F]    => Coproduct[FieldExpr[F]](e)
    case e: ConstExpr[F]   => Coproduct[FieldExpr[F]](e)
    case e: BetweenExpr[F] => Coproduct[FieldExpr[F]](e)
    case e: SeveralExpr[F] => Coproduct[FieldExpr[F]](e)
    case e: EveryExpr[F]   => Coproduct[FieldExpr[F]](e)
  }

}
