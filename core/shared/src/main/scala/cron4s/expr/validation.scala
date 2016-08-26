package cron4s.expr

import cron4s.{CronField, CronUnit}
import cron4s.types._

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 02/08/2016.
  */
private[expr] object validation {

  type ValidatedExpr[E[_] <: Expr[F], F <: CronField] = ValidationNel[String, E[F]]

  def validateSeveral[E[_] <: Expr[F], F <: CronField](exprs: NonEmptyList[E[F]])
      (implicit unit: CronUnit[F], ev: SeqEnumerableExpr[E, F]): ValidatedExpr[SeveralExpr[E, ?], F] = {

    def validateImplication(expr: E[F],
                            processed: Vector[E[F]]
                           ): ValidationNel[String, E[F]] = {
      val alreadyImplied = processed.find(e => expr.impliedBy(e)).
        map(found => s"Expression $expr is implied by $found".failureNel[E[F]])
      val impliesOther = processed.find(_.impliedBy(expr)).
        map(found => s"Expression $found is implied by $expr".failureNel[E[F]])

      alreadyImplied.orElse(impliesOther).getOrElse(expr.successNel[String])
    }

    val zero = Vector.empty[E[F]]
    val (_, result) = exprs.foldLeft((zero, zero.successNel[String])) { case ((seen, acc), expr) =>
      val validated = (acc |@| validateImplication(expr, seen))((prev, next) => prev :+ next)
      (seen :+ expr, validated)
    }
    result.map(vec => SeveralExpr[E, F](vec.sorted))
  }

}
