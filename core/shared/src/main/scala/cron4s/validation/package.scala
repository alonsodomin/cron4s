package cron4s

import cron4s.expr._
import cron4s.types.IsFieldExpr

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 30/08/2016.
  */
package object validation {

  def validateCron(ast: CronExprAST): Either[ValidationError, CronExpr] = {
    val errors = ast.map(generic.validate).toList.flatMap(identity)
    if (errors.size > 0) {
      Left(ValidationError(NonEmptyList(errors.head, errors.tail: _*)))
    } else Right(null)
  }

  type ValidatedExpr[E[_ <: CronField], F <: CronField] = ValidationNel[InvalidFieldExpr[F], E[F]]

  /*def validateSeveral[F <: CronField]
      (exprs: NonEmptyList[EnumerableExpr[F]])
      (implicit unit: CronUnit[F], ops: IsFieldExpr[EnumerableExpr, F]): ValidatedExpr[SeveralExpr, F] = {

    def implicationErrorMsg(that: EnumerableExpr[F], impliedBy: EnumerableExpr[F]): String =
      s"Expression '$that' at field ${that.unit.field} is implied by '$impliedBy'"

    def validateImplication(
        expr: EnumerableExpr[F],
        processed: Vector[EnumerableExpr[F]]
    ): ValidatedExpr[EnumerableExpr, F] = {
      val alreadyImplied = processed.find(e => ops.impliedBy(expr)(e)).
        map(found => InvalidFieldExpr(expr.unit.field, implicationErrorMsg(expr, found)).failureNel[EnumerableExpr[F]])
      val impliesOther = processed.find(e => ops.impliedBy(e)(expr)).
        map(found => InvalidFieldExpr(expr.unit.field, implicationErrorMsg(found, expr)).failureNel[EnumerableExpr[F]])

      alreadyImplied.orElse(impliesOther).getOrElse(expr.successNel[InvalidFieldExpr[F]])
    }

    val zero = Vector.empty[EnumerableExpr[F]]
    val (_, result) = exprs.foldLeft((zero, zero.successNel[InvalidFieldExpr[F]])) { case ((seen, acc), expr) =>
      val validated = (acc |@| validateImplication(expr, seen))((prev, next) => prev :+ next)
      (seen :+ expr, validated)
    }
    //result.map(vec => SeveralExpr[F](vec.sorted))
    ???
  }*/

}
