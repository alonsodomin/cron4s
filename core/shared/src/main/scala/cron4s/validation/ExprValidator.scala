package cron4s.validation

import cron4s.{CronField, CronUnit, InvalidFieldExpr, generic}
import cron4s.expr._
import cron4s.types.{HasCronField, IsFieldExpr}
import cron4s.syntax.expr._
import shapeless.Coproduct

import scalaz._
import scalaz.syntax.applicative._
import scalaz.syntax.validation._

/**
  * Created by alonsodomin on 18/12/2016.
  */
trait ExprValidator[E[_ <: CronField], F <: CronField] {

  def validate(expr: E[F]): ValidationNel[InvalidFieldExpr[F], E[F]]

}

object ExprValidator extends ExprValidatorInstances {
  def apply[E[_ <: CronField], F <: CronField](implicit ev: ExprValidator[E, F]): ExprValidator[E, F] = ev
}

trait ExprValidatorInstances extends LowPriorityExprValidatorInstances {

  implicit def eachValidator[F <: CronField]: ExprValidator[EachExpr, F] = new ExprValidator[EachExpr, F] {

    def validate(expr: EachExpr[F]): ValidationNel[InvalidFieldExpr[F], EachExpr[F]] =
      expr.successNel[InvalidFieldExpr[F]]
  }

  implicit def constValidator[F <: CronField](implicit ev: HasCronField[CronUnit, F]): ExprValidator[ConstExpr, F] = new ExprValidator[ConstExpr, F] {
    def validate(expr: ConstExpr[F]): ValidationNel[InvalidFieldExpr[F], ConstExpr[F]] = {
      if (expr.value < expr.unit.min || expr.value > expr.unit.max) {
        InvalidFieldExpr[F](
          expr.unit.field,
          s"Value ${expr.value} is out of bounds for field: ${expr.unit.field}"
        ).failureNel[ConstExpr[F]]
      } else expr.successNel[InvalidFieldExpr[F]]
    }
  }

  implicit def betweenValidator[F <: CronField](implicit ev: HasCronField[CronUnit, F]): ExprValidator[BetweenExpr, F] = new ExprValidator[BetweenExpr, F] {
    def validate(expr: BetweenExpr[F]): ValidationNel[InvalidFieldExpr[F], BetweenExpr[F]] = {
      val rangeValid = {
        if (expr.begin.value > expr.end.value) expr.successNel[InvalidFieldExpr[F]]
        else InvalidFieldExpr[F](expr.unit.field, s"${expr.begin.value} should be less than ${expr.end.value}").failureNel[BetweenExpr[F]]
      }

      val elemsValid = (constValidator[F].validate(expr.begin) |@| constValidator[F].validate(expr.end))((_, _) => expr)
      (elemsValid |@| rangeValid)((l, _) => l)
    }
  }

  implicit def severalValidator[F <: CronField]: ExprValidator[SeveralExpr, F] = new ExprValidator[SeveralExpr, F] {
    override def validate(expr: SeveralExpr[F]): ValidationNel[InvalidFieldExpr[F], SeveralExpr[F]] = ???
  }

  implicit def everyValidator[F <: CronField]: ExprValidator[EveryExpr, F] = new ExprValidator[EveryExpr, F] {
    override def validate(expr: EveryExpr[F]): ValidationNel[InvalidFieldExpr[F], EveryExpr[F]] = ???
  }

}

trait LowPriorityExprValidatorInstances {
  /*implicit def enumValidator[F <: CronField]: ExprValidator[EnumExprAST, F] = new ExprValidator[EnumExprAST, F] {
    def validate(expr: EnumExprAST[F]): ValidationNel[InvalidFieldExpr[F], EnumExprAST[F]] = {
      val folded = expr.fold(generic.validate)
      expr.fold(generic.validate).map(res => Coproduct[EnumExprAST[F]](res))
    }
  }*/

  /*implicit def divValidator[F <: CronField]: ExprValidator[DivExprAST, F] = new ExprValidator[DivExprAST, F] {
    def validate(expr: DivExprAST[F]): ValidationNel[InvalidFieldExpr[F], DivExprAST[F]] =
      expr.fold(generic.validate).map(res => Coproduct[DivExprAST[F]](res))
  }

  implicit def fieldValidator[F <: CronField]: ExprValidator[FieldExprAST, F] = new ExprValidator[FieldExprAST, F] {
    def validate(expr: FieldExprAST[F]): ValidationNel[InvalidFieldExpr[F], FieldExprAST[F]] =
      expr.fold(generic.validate).map(res => Coproduct[FieldExprAST[F]](res))
  }*/
}
