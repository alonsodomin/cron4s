package cron4s.expr

import cron4s.{CronField, CronUnit}

import shapeless.HList
import shapeless.ops.hlist.Selector

/**
  * Created by domingueza on 29/07/2016.
  */
final case class CronExpr(repr: CronExprRepr) {

  class GetField[F <: CronField] {
    def apply[L <: HList](expr: L)(implicit selector: Selector[L, Expr[F]]): Expr[F] =
      expr.select[Expr[F]]
  }

  lazy val minutes = repr.select[Expr[CronField.Minute.type]]
  lazy val hours = repr.select[Expr[CronField.Hour.type]]
  lazy val daysOfMonth = repr.select[Expr[CronField.DayOfMonth.type]]
  lazy val months = repr.select[Expr[CronField.Month.type]]
  lazy val daysOfWeek = repr.select[Expr[CronField.DayOfWeek.type]]

  def field[F <: CronField : CronUnit](f: F): Expr[F] = f match {
    case CronField.Minute     => minutes.asInstanceOf[Expr[F]]
    case CronField.Hour       => hours.asInstanceOf[Expr[F]]
    case CronField.DayOfMonth => daysOfMonth.asInstanceOf[Expr[F]]
    case CronField.Month      => months.asInstanceOf[Expr[F]]
    case CronField.DayOfWeek  => daysOfWeek.asInstanceOf[Expr[F]]
  }

}
