package cron4s

import cron4s.expr._

import scalaz._

/**
  * Created by alonsodomin on 30/08/2016.
  */
package object validation {

  def validateCron(ast: CronExprAST): Either[ValidationError, CronExpr] = {
    val errors = ast.map(generic.validate).toList.flatten
    if (errors.size > 0) {
      Left(ValidationError(NonEmptyList(errors.head, errors.tail: _*)))
    } else Right(CronExpr(ast))
  }

}
