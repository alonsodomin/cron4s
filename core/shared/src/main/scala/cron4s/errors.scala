package cron4s

/**
  * Created by alonsodomin on 30/08/2016.
  */
sealed trait ParseFailure

final case class InvalidExpression[F <: CronField](field: F, msg: String)
