package cron4s

import cron4s.expr.{EnumerableExpr, SeveralExpr}
import cron4s.types.IsFieldExpr

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 30/08/2016.
  */
package object validation {

  type ValidatedExpr[E[_ <: CronField], F <: CronField] = ValidationNel[InvalidExpression[F], E[F]]

  type ValidationHandler[E[_ <: CronField], F <: CronField, R] = PartialFunction[ValidatedExpr[E, F], R]

  def validateSeveral[F <: CronField]
      (exprs: NonEmptyList[EnumerableExpr[F]])
      (implicit unit: CronUnit[F], ops: IsFieldExpr[EnumerableExpr, F]): ValidatedExpr[SeveralExpr, F] = {

    def validateImplication(
        expr: EnumerableExpr[F],
        processed: Vector[EnumerableExpr[F]]
    ): ValidatedExpr[EnumerableExpr, F] = {
      val alreadyImplied = processed.find(e => ops.impliedBy(expr)(e)).
        map(found => InvalidExpression(expr.unit.field, s"Expression $expr is implied by $found").failureNel[EnumerableExpr[F]])
      val impliesOther = processed.find(e => ops.impliedBy(e)(expr)).
        map(found => InvalidExpression(expr.unit.field, s"Expression $found is implied by $expr").failureNel[EnumerableExpr[F]])

      alreadyImplied.orElse(impliesOther).getOrElse(expr.successNel[InvalidExpression[F]])
    }

    val zero = Vector.empty[EnumerableExpr[F]]
    val (_, result) = exprs.foldLeft((zero, zero.successNel[InvalidExpression[F]])) { case ((seen, acc), expr) =>
      val validated = (acc |@| validateImplication(expr, seen))((prev, next) => prev :+ next)
      (seen :+ expr, validated)
    }
    result.map(vec => SeveralExpr[F](vec.sorted))
  }

}
