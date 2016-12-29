package cron4s.expr

import cron4s.{CronField, CronUnit, generic}
import shapeless._

/**
  * Representation of a valid CRON expression as an AST
  *
  * @author Antonio Alonso Dominguez
  */
object CronExpr {

  def apply(
    seconds: SecondsNode,
    minutes: MinutesNode,
    hours: HoursNode,
    daysOfMonth: DaysOfMonthNode,
    months: MonthsNode,
    daysOfWeek: DaysOfWeekNode
  ): CronExpr = CronExpr(
    seconds :: minutes :: hours :: daysOfMonth :: months :: daysOfWeek :: HNil
  )
  
}

final case class CronExpr(ast: CronExprAST) {

  def seconds: SecondsNode = ast.select[SecondsNode]
  def minutes: MinutesNode = ast.select[MinutesNode]
  def hours: HoursNode = ast.select[HoursNode]
  def daysOfMonth: DaysOfMonthNode = ast.select[DaysOfMonthNode]
  def months: MonthsNode = ast.select[MonthsNode]
  def daysOfWeek: DaysOfWeekNode = ast.select[DaysOfWeekNode]

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
  def field[F <: CronField](implicit unit: CronUnit[F]): FieldNode[F] = unit.field match {
    case CronField.Second     => seconds.asInstanceOf[FieldNode[F]]
    case CronField.Minute     => minutes.asInstanceOf[FieldNode[F]]
    case CronField.Hour       => hours.asInstanceOf[FieldNode[F]]
    case CronField.DayOfMonth => daysOfMonth.asInstanceOf[FieldNode[F]]
    case CronField.Month      => months.asInstanceOf[FieldNode[F]]
    case CronField.DayOfWeek  => daysOfWeek.asInstanceOf[FieldNode[F]]
  }

  def ranges: List[IndexedSeq[Int]] = ast.map(generic.ops.range).toList

  override def toString = ast.map(generic.ops.show).toList.mkString(" ")

}
