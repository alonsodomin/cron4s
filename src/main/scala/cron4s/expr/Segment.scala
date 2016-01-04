package cron4s.expr

import java.time.temporal.{TemporalField, ChronoField, TemporalAccessor}

import CronField._
import cats.syntax.contravariant._
import cron4s.matcher.Matcher

/**
  * Created by alonsodomin on 03/01/2016.
  */
abstract class Segment[V: Value, F <: CronField](expr: Part[V, F]) {

  def matcher: Matcher[TemporalAccessor]

}

object Segment {

  abstract class NumericSegment[F <: CronField](expr: Part[Int, F], temporalField: TemporalField) extends Segment[Int, F](expr) {

    def matcher: Matcher[TemporalAccessor] = expr.matcher.contramap(ta => ta.get(temporalField))

  }

  abstract class TextSegment[F <: CronField](expr: Part[String, F], temporalField: TemporalField) extends Segment[String, F](expr) {

    override def matcher: Matcher[TemporalAccessor] = expr.matcher.contramap { ta =>
      val monthVal = ta.get(temporalField)
      expr.unit(monthVal).getOrElse("")
    }

  }

  implicit final class Minutes(expr: Part[Int, Minute.type]) extends NumericSegment[Minute.type](expr, ChronoField.MINUTE_OF_HOUR)
  implicit final class Hours(expr: Part[Int, Hour.type]) extends NumericSegment[Hour.type](expr, ChronoField.HOUR_OF_DAY)
  implicit final class DaysOfMonth(expr: Part[Int, DayOfMonth.type]) extends NumericSegment[DayOfMonth.type](expr, ChronoField.DAY_OF_MONTH)

  abstract class Months[V: Value](expr: Part[V, Month.type]) extends Segment[V, Month.type](expr)
  implicit final class NumericMonths(expr: Part[Int, Month.type]) extends Months[Int](expr) {
    def matcher: Matcher[TemporalAccessor] = expr.matcher.contramap(ta => ta.get(ChronoField.MONTH_OF_YEAR))
  }
  implicit final class TextMonths(expr: Part[String, Month.type]) extends Months[String](expr) {
    def matcher: Matcher[TemporalAccessor] = expr.matcher.contramap { ta =>
      val monthVal = ta.get(ChronoField.MONTH_OF_YEAR)
      expr.unit(monthVal).getOrElse("")
    }
  }

  abstract class DaysOfWeeks[V: Value](expr: Part[V, DayOfWeek.type]) extends Segment[V, DayOfWeek.type](expr)
  implicit final class NumericDaysOfWeek(expr: Part[Int, DayOfWeek.type]) extends DaysOfWeeks[Int](expr) {
    def matcher: Matcher[TemporalAccessor] = expr.matcher.contramap(ta => ta.get(ChronoField.DAY_OF_WEEK))
  }
  implicit final class TextDaysOfWeek(expr: Part[String, DayOfWeek.type]) extends DaysOfWeeks[String](expr) {
    def matcher: Matcher[TemporalAccessor] = expr.matcher.contramap { ta =>
      val monthVal = ta.get(ChronoField.DAY_OF_WEEK)
      expr.unit(monthVal).getOrElse("")
    }
  }

}
