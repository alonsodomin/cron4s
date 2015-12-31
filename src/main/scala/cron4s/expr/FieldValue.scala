package cron4s.expr

/**
  * Created by domingueza on 31/12/15.
  */
case class FieldValue[V: Value, U <: CronUnit](value: V, unit: U)(implicit val unitOps: CronUnitOps[V, U])
