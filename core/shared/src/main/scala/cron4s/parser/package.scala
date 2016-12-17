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

  private[this] val digit = P(CharIn('0' to '9'))
  private[this] val sexagesimal = P(((CharIn('1' to '5') ~ digit) | ("0" ~ digit) | digit).!).map(_.toInt)

  //----------------------------------------
  // Individual Expression Atoms
  //----------------------------------------

  // Seconds

  val seconds: Parser[ConstExpr[Second]] = sexagesimal.map(value => ConstExpr[Second](value))

  // Minutes

  val minutes: Parser[ConstExpr[Minute]] = sexagesimal.map(value => ConstExpr[Minute](value))

  // Hours

  val hours: Parser[ConstExpr[Hour]] = P((("2" ~ CharIn('0' to '3')) | ("0" | "1") ~ digit | digit).!).map {
    value => ConstExpr[Hour](value.toInt)
  }

  // Day Of Month

  val daysOfMonth: Parser[ConstExpr[DayOfMonth]] = P(((("0" | "1" | "2") ~ digit) | ("3" ~ ("0" | "1")) | digit).!).map {
    value => ConstExpr[DayOfMonth](value.toInt)
  }

  // Month

  private[this] val numericMonth = P((("1" ~ CharIn('0' to '2')) | ("0".? ~ digit)).!).map {
    value => ConstExpr[Month](value.toInt)
  }.opaque("numeric month")
  private[this] val textualMonth = P(StringIn(Months.textValues: _*).!).map { value =>
    val index = Months.textValues.indexOf(value)
    ConstExpr[Month](index + 1, Some(value))
  }

  val months: Parser[ConstExpr[Month]] = textualMonth | numericMonth

  // Day Of Week

  private[this] val numericDayOfWeek = P(CharIn('0' to '6').!)
    .map(value => ConstExpr[DayOfWeek](value.toInt))
  private[this] val textualDayOfWeek = P(StringIn(DaysOfWeek.textValues: _*).!).map { value =>
    val index = DaysOfWeek.textValues.indexOf(value)
    ConstExpr[DayOfWeek](index, Some(value))
  }
  val daysOfWeek: Parser[ConstExpr[DayOfWeek]] = numericDayOfWeek | textualDayOfWeek

  //----------------------------------------
  // Field-Based Expression Atoms
  //----------------------------------------

  def each[F <: CronField](implicit unit: CronUnit[F]): Parser[AnyExpr[F]] =
    P("*").map(_ => AnyExpr[F])

  def between[F <: CronField](p: Parser[ConstExpr[F]])(implicit unit: CronUnit[F]): Parser[BetweenExpr[F]] =
    (p ~ p).map { case (min, max) => BetweenExpr[F](min, max) }

  def several[F <: CronField](p: Parser[EnumerableExpr[F]])(implicit unit: CronUnit[F]): Parser[SeveralExpr[F]] =
    p.rep(min = 1, sep = ",")
      .map(values => SeveralExpr[F](NonEmptyList(values.head, values.tail: _*)))

  def every[F <: CronField](p: Parser[DivisibleExpr[F]])(implicit unit: CronUnit[F]): Parser[EveryExpr[F]] =
    (p ~ "/" ~/ CharIn('0' to '9').rep(1).!).map { case (base, freq) => EveryExpr[F](base, freq.toInt) }

  //----------------------------------------
  // AST Parsing & Building
  //----------------------------------------

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
