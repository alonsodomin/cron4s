package cron4s.expr

/**
  * Created by alonsodomin on 18/12/2015.
  */
class CronField[E : Expr, U <: CronUnit](unit: U, expr: E) {

}
