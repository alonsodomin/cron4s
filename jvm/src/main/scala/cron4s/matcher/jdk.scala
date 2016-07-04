package cron4s.matcher

import java.time.temporal.{ChronoField, TemporalAccessor, TemporalField}

import cats.std.list._
import cron4s.expr._

/**
  * Created by alonsodomin on 04/01/2016.
  */
object jdk {
  import CronField._

  implicit def temporalAccessor[T <: TemporalAccessor](field: CronField, accessor: T): Option[Int] = {
    val offset = if (field == DayOfWeek) -1 else 0
    val temporalField = cronField2TemporalField(field)

    if (!accessor.isSupported(temporalField)) None
    else Some(accessor.get(cronField2TemporalField(field)) + offset)
  }

  implicit class CronExprMatcher(expr: CronExpr) {

    private[this] def matchers[T <: TemporalAccessor] = List(
      expr.minutes.matcherFor[T],
      expr.hours.matcherFor[T],
      expr.daysOfMonth.matcherFor[T],
      expr.month.matcherFor[T],
      expr.daysOfWeek.matcherFor[T]
    )

    def matcherFor[T <: TemporalAccessor]: Matcher[T] = {
      import Matcher._
      import conjunction._

      forall(List(
        expr.minutes.matcherFor[T],
        expr.hours.matcherFor[T],
        expr.daysOfMonth.matcherFor[T],
        expr.month.matcherFor[T],
        expr.daysOfWeek.matcherFor[T]
      ))
    }

    def matches[T <: TemporalAccessor](t: T): Boolean =
      matcherFor[T].matches(t)

  }

  private[this] def cronField2TemporalField(field: CronField): TemporalField = field match {
    case Minute     => ChronoField.MINUTE_OF_HOUR
    case Hour       => ChronoField.HOUR_OF_DAY
    case DayOfMonth => ChronoField.DAY_OF_MONTH
    case Month      => ChronoField.MONTH_OF_YEAR
    case DayOfWeek  => ChronoField.DAY_OF_WEEK
  }

}
