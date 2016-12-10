package cron4s.expr

class DatePartExpr private[expr] (val underlying: DatePartRepr) extends AnyVal {

  def daysOfMonth: DaysOfMonthExpr = underlying.select[DaysOfMonthExpr]
  def months: MonthsExpr = underlying.select[MonthsExpr]
  def daysOfWeek: DaysOfWeekExpr = underlying.select[DaysOfWeekExpr]

  override def toString = s"$daysOfMonth $months $daysOfWeek"

}

class TimePartExpr private[expr] (val underlying: TimePartRepr) extends AnyVal {

  def seconds: SecondExpr = underlying.select[SecondExpr]
  def minutes: MinutesExpr = underlying.select[MinutesExpr]
  def hours: HoursExpr = underlying.select[HoursExpr]

  override def toString = s"$seconds $minutes $hours"

}