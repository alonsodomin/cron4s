package cron4s

import scala.util.parsing.input.Position

/**
  * Created by alonsodomin on 30/08/2016.
  */
final case class ParseError(message: String, input: String, position: Position) {
  override def toString = message + ":\n\n" + position.longString
}

final case class InvalidExpression[F <: CronField](field: F, msg: String)
