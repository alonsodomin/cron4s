package cron4s

import cron4s.expr._

import fastparse.all._

import scalaz.NonEmptyList

/**
  * Created by alonsodomin on 15/12/2016.
  */
package object parser {
  import CronField._
  import CronUnit._

  val seconds: Parser[ConstExpr[Second]] = P((("0" | CharIn('1' to '5')).? ~ CharIn('0' to '9')).!).map {
    value => ConstExpr[Second](value.toInt)
  }

  val minutes: Parser[ConstExpr[Minute]] = P((("0" | CharIn('1' to '5')).? ~ CharIn('0' to '9')).!).map {
    value => ConstExpr[Minute](value.toInt)
  }

  val hours: Parser[ConstExpr[Hour]] = P((("2" ~ CharIn('0' to '3')) | ("0" | "1").? ~ CharIn('0' to '9')).!).map {
    value => ConstExpr[Hour](value.toInt)
  }

  val daysOfMonth: Parser[ConstExpr[DayOfMonth]] = P((("3" ~ ("0" | "1")) | (("0" | "1" | "2").? ~ CharIn('0' to '9'))).!).map {
    value => ConstExpr[DayOfMonth](value.toInt)
  }

  private[this] val numericMonth = P((("1" ~ CharIn('0' to '2')) | ("0".? ~ CharIn('1' to '9'))).!).map {
    value => ConstExpr[Month](value.toInt)
  }.opaque("numeric month")
  private[this] val textualMonth = Months.textValues
    .map(name => P(name).!)
    .reduce(_ | _)
    .map { value =>
      val index = Months.textValues.indexOf(value)
      ConstExpr[Month](index, Some(value))
    }.opaque("month name")
  val months: Parser[ConstExpr[Month]] = numericMonth | textualMonth

  private[this] val numericDayOfWeek = P(CharIn('0' to '6').!)
    .map(value => ConstExpr[DayOfWeek](value.toInt))
  private[this] val textualDayOfWeek = DaysOfWeek.textValues
    .map(name => P(name).!)
    .reduce(_ | _)
    .map { value =>
      val index = DaysOfWeek.textValues.indexOf(value)
      ConstExpr[DayOfWeek](index, Some(value))
    }.opaque("day of week name")
  val daysOfWeek: Parser[ConstExpr[DayOfWeek]] = numericDayOfWeek | textualDayOfWeek

  def each[F <: CronField](implicit unit: CronUnit[F]): Parser[AnyExpr[F]] =
    P("*").map(_ => AnyExpr[F])

  def between[F <: CronField](p: Parser[ConstExpr[F]])(implicit unit: CronUnit[F]): Parser[BetweenExpr[F]] =
    (p ~ p).map { case (min, max) => BetweenExpr[F](min, max) }

  // TODO allow the error to propagate through the parser
  def several[F <: CronField](p: Parser[EnumerableExpr[F]])(implicit unit: CronUnit[F]): Parser[SeveralExpr[F]] =
    p.rep(min = 1, sep = ",")
      .map(values => SeveralExpr[F](NonEmptyList(values.head, values.tail: _*)))
  //.map(_.toEither.right.get)

  def every[F <: CronField](p: Parser[DivisibleExpr[F]])(implicit unit: CronUnit[F]): Parser[EveryExpr[F]] =
    (p ~ "/" ~/ CharIn('0' to '9').rep(1).!).map { case (base, freq) => EveryExpr[F](base, freq.toInt) }

  def of[F <: CronField](p: Parser[ConstExpr[F]])(implicit unit: CronUnit[F]): Parser[Expr[F]] = {
    val severalExpr = several(between(p) | p)
    val everyExpr = every(severalExpr | between(p) | each[F])

    everyExpr | severalExpr | between(p) | p | each
  }

  val cron: Parser[CronExpr] = P(
    Start ~ of(seconds) ~ " " ~/ of(minutes) ~ " " ~/ of(hours) ~ " " ~/
      of(daysOfMonth) ~ " " ~/ of(months) ~ " " ~/ of(daysOfWeek) ~ End
  ).map { case (sec, min, hour, day, month, weekDay) =>
    CronExpr(sec, min, hour, day, month, weekDay)
  }

}
