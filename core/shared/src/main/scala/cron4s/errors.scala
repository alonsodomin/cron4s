package cron4s

import scala.util.parsing.input.Position
import scalaz.NonEmptyList

/**
  * Created by alonsodomin on 30/08/2016.
  */
sealed trait ExpressionError

final case class ParseFailed(message: String, input: String, position: Position) extends ExpressionError {
  override def toString = message + ":\n\n" + position.longString
}

final case class InvalidCronExpr(invalidFields: NonEmptyList[InvalidExpr]) extends ExpressionError
final case class InvalidExpr(field: CronField, msg: String)
final case class InvalidFieldExpr[F <: CronField](field: F, msg: String) extends ExpressionError
