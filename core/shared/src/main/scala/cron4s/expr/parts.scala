package cron4s.expr

class DatePartExpr(val underlying: DatePartRepr) extends AnyVal {

  def daysOfMonth: DaysOfMonthExpr = underlying.select[DaysOfMonthExpr]
  def months: MonthsExpr = underlying.select[MonthsExpr]
  def daysOfWeekExpr: DaysOfWeekExpr = underlying.select[DaysOfWeekExpr]

  override def toString = s"$daysOfMonth $months $daysOfWeekExpr"

}

class TimePartExpr(val underlying: TimePartRepr) extends AnyVal {

  def seconds: SecondExpr = underlying.select[SecondExpr]
  def minutes: MinutesExpr = underlying.select[MinutesExpr]
  def hours: HoursExpr = underlying.select[HoursExpr]

  override def toString = s"$seconds $minutes $hours"

}