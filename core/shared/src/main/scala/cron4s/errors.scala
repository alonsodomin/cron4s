package cron4s


/**
  * Created by alonsodomin on 30/08/2016.
  */
sealed trait InvalidCron

final case class ParseFailed(msg: String, position: Int) extends InvalidCron
final case class InvalidField(field: CronField, msg: String)
final case class InvalidFieldExpr[F <: CronField](field: F, msg: String) extends InvalidCron
