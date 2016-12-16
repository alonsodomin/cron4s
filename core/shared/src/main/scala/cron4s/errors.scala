package cron4s


/**
  * Created by alonsodomin on 30/08/2016.
  */
sealed trait ParseFailed

final case class InvalidCron(msg: String) extends ParseFailed
final case class InvalidField(field: CronField, msg: String)
final case class InvalidFieldExpr[F <: CronField](field: F, msg: String) extends ParseFailed
