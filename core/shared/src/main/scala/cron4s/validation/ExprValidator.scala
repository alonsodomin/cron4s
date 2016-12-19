package cron4s.validation

import cron4s.{CronField, CronUnit, FieldError, generic}
import cron4s.expr._
import cron4s.types.HasCronField
import cron4s.syntax.field._
import cron4s.syntax.expr._

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 18/12/2016.
  */
trait ExprValidator[E[_ <: CronField], F <: CronField] {

  def validate(expr: E[F]): List[FieldError]

}

object ExprValidator extends ExprValidatorInstances {
  def apply[E[_ <: CronField], F <: CronField](implicit ev: ExprValidator[E, F]): ExprValidator[E, F] = ev
}

trait ExprValidatorInstances extends LowPriorityExprValidatorInstances {

  implicit def eachValidator[F <: CronField]: ExprValidator[EachExpr, F] = new ExprValidator[EachExpr, F] {
    def validate(expr: EachExpr[F]): List[FieldError] = List.empty
  }

  implicit def constValidator[F <: CronField](implicit
      ev: HasCronField[CronUnit, F]
    ): ExprValidator[ConstExpr, F] = new ExprValidator[ConstExpr, F] {

      def validate(expr: ConstExpr[F]): List[FieldError] = {
        if (expr.value < expr.unit.min || expr.value > expr.unit.max) {
          List(FieldError(
            expr.unit.field,
            s"Value ${expr.value} is out of bounds for field: ${expr.unit.field}"
          ))
        } else List.empty
      }

    }

  implicit def betweenValidator[F <: CronField](implicit
      hasCronField: HasCronField[CronUnit, F]
    ): ExprValidator[BetweenExpr, F] = new ExprValidator[BetweenExpr, F] {
      def validate(expr: BetweenExpr[F]): List[FieldError] = {
        val rangeValid = {
          if (expr.begin.value <= expr.end.value)
            List(FieldError(expr.unit.field, s"${expr.begin.value} should be less than ${expr.end.value}"))
          else List.empty
        }

        constValidator[F].validate(expr.begin) ++ constValidator[F].validate(expr.end) ++ rangeValid
      }
  }

  implicit def severalValidator[F <: CronField](implicit
    hasCronField: HasCronField[CronUnit, F]
  ): ExprValidator[SeveralExpr, F] = new ExprValidator[SeveralExpr, F] {
    def validate(expr: SeveralExpr[F]): List[FieldError] = {
      def implicationErrorMsg(that: EnumExprAST[F], impliedBy: EnumExprAST[F]): String =
        s"Expression '${that.show}' at field ${that.unit.field} is implied by '${impliedBy.show}'"

      def verifyImplication(seen: List[EnumExprAST[F]], curr: EnumExprAST[F]): Option[FieldError] = {
        val alreadyImplied = seen.find(e => curr.impliedBy(e))
          .map(found => FieldError(curr.unit.field, implicationErrorMsg(curr, found)))
        val impliesOther = seen.find(_.impliedBy(curr))
          .map(found => FieldError(curr.unit.field, implicationErrorMsg(found, curr)))

        alreadyImplied.orElse(impliesOther)
      }

      val zero = (List.empty[EnumExprAST[F]], List.empty[FieldError])
      val (_, errorResult) = expr.values.foldRight(zero) { case (expr, (seen, errors)) =>
        val newErrors = verifyImplication(seen, expr).toList ::: errors
        val newSeen = expr :: seen
        newSeen -> newErrors
      }

      errorResult
    }
  }

  implicit def everyValidator[F <: CronField]: ExprValidator[EveryExpr, F] = new ExprValidator[EveryExpr, F] {
    override def validate(expr: EveryExpr[F]): List[FieldError] = List.empty
  }

}

trait LowPriorityExprValidatorInstances {

  implicit def enumValidator[F <: CronField](
      implicit
      ev: HasCronField[CronUnit, F]
    ): ExprValidator[EnumExprAST, F] = new ExprValidator[EnumExprAST, F] {
      def validate(expr: EnumExprAST[F]): List[FieldError] =
        expr.fold(generic.validate)
    }

  implicit def divValidator[F <: CronField](
      implicit
      ev: HasCronField[CronUnit, F]
    ): ExprValidator[DivExprAST, F] = new ExprValidator[DivExprAST, F] {
      def validate(expr: DivExprAST[F]): List[FieldError] =
        expr.fold(generic.validate)
    }

  implicit def fieldValidator[F <: CronField](
      implicit
      ev: HasCronField[CronUnit, F]
    ): ExprValidator[FieldExprAST, F] = new ExprValidator[FieldExprAST, F] {
      def validate(expr: FieldExprAST[F]): List[FieldError] =
        expr.fold(generic.validate)
    }
}
