package cron4s.expr

import cron4s.{CronField, CronUnit}
import cron4s.types._

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 02/08/2016.
  */
private[expr] object validation {
  import Expr._

  type ValidatedExpr[E[_] <: Expr[F], F <: CronField] = ValidationNel[String, E[F]]

  def validateSeveral[F <: CronField](exprs: NonEmptyList[EnumerableExpr[F]])
      (implicit unit: CronUnit[F], ops: IsFieldExpr[EnumerableExpr, F]): ValidatedExpr[SeveralExpr, F] = {

    def validateImplication(expr: EnumerableExpr[F],
                            processed: Vector[EnumerableExpr[F]]
                           ): ValidationNel[String, EnumerableExpr[F]] = {
      val alreadyImplied = processed.find(e => ops.impliedBy(expr)(e)).
        map(found => s"Expression $expr is implied by $found".failureNel[EnumerableExpr[F]])
      val impliesOther = processed.find(e => ops.impliedBy(e)(expr)).
        map(found => s"Expression $found is implied by $expr".failureNel[EnumerableExpr[F]])

      alreadyImplied.orElse(impliesOther).getOrElse(expr.successNel[String])
    }

    val zero = Vector.empty[EnumerableExpr[F]]
    val (_, result) = exprs.foldLeft((zero, zero.successNel[String])) { case ((seen, acc), expr) =>
      val validated = (acc |@| validateImplication(expr, seen))((prev, next) => prev :+ next)
      (seen :+ expr, validated)
    }
    result.map(vec => SeveralExpr[F](vec.sorted))
  }

}
