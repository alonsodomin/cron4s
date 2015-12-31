package cron4s.expr

/**
  * Created by alonsodomin on 31/12/2015.
  */
class Field[V: Value, U <: CronUnit](expr: Expr[V], unit: U) {

  def min: V = expr.max(unit)

}
