package cron4s.expr

import cron4s.{CronField, CronUnit}

import shapeless._

/**
  * Representation of a valid CRON expression as an AST
  *
  * @author Antonio Alonso Dominguez
  */
final case class CronExpr(
    seconds: FieldExprAST[CronField.Second],
    minutes: FieldExprAST[CronField.Minute],
    hours: FieldExprAST[CronField.Hour],
    daysOfMonth: FieldExprAST[CronField.DayOfMonth],
    months: FieldExprAST[CronField.Month],
    daysOfWeek: FieldExprAST[CronField.DayOfWeek]
  ) {

  private[cron4s] lazy val ast: CronExprAST = Generic[CronExpr].to(this)

  /**
    * Time part of the CRON expression
    */
  lazy val timePart: TimePartExpr = TimePartExpr(seconds, minutes, hours)

  /**
    * Date part of the CRON expression
    */
  lazy val datePart: DatePartExpr = DatePartExpr(daysOfMonth, months, daysOfWeek)

  /**
    * Generic field accessor. Given a CronField, this method can be used
    * to access the expression on that given field.
    *
    * @param unit the CronUnit for the given field
    * @tparam F CronField type
    * @return field-based expression for given field
    */
  def field[F <: CronField](implicit unit: CronUnit[F]): FieldExprAST[F] = unit.field match {
    case CronField.Second     => seconds.asInstanceOf[FieldExprAST[F]]
    case CronField.Minute     => minutes.asInstanceOf[FieldExprAST[F]]
    case CronField.Hour       => hours.asInstanceOf[FieldExprAST[F]]
    case CronField.DayOfMonth => daysOfMonth.asInstanceOf[FieldExprAST[F]]
    case CronField.Month      => months.asInstanceOf[FieldExprAST[F]]
    case CronField.DayOfWeek  => daysOfWeek.asInstanceOf[FieldExprAST[F]]
  }

  def ranges: List[Vector[Int]] = ast.map(cron4s.generic.range).toList

  override def toString = ast.map(cron4s.generic.show).toList.mkString(" ")

}
