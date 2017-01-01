package cron4s

import cron4s.expr._

import fastparse.all._

import shapeless._

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

  val seconds: Parser[ConstNode[Second]] = sexagesimal.map(value => ConstNode[Second](value))

  // Minutes

  val minutes: Parser[ConstNode[Minute]] = sexagesimal.map(value => ConstNode[Minute](value))

  // Hours

  val hours: Parser[ConstNode[Hour]] = P((("2" ~ CharIn('0' to '3')) | ("0" | "1") ~ digit | digit).!).map {
    value => ConstNode[Hour](value.toInt)
  }

  // Day Of Month

  val daysOfMonth: Parser[ConstNode[DayOfMonth]] = P(((("0" | "1" | "2") ~ digit) | ("3" ~ ("0" | "1")) | digit).!).map {
    value => ConstNode[DayOfMonth](value.toInt)
  }

  // Month

  private[this] val numericMonth = P((("1" ~ CharIn('0' to '2')) | ("0".? ~ digit)).!).map {
    value => ConstNode[Month](value.toInt)
  }.opaque("numeric month")
  private[this] val textualMonth = P(StringIn(Months.textValues: _*).!).map { value =>
    val index = Months.textValues.indexOf(value)
    ConstNode[Month](index + 1, Some(value))
  }

  val months: Parser[ConstNode[Month]] = textualMonth | numericMonth

  // Day Of Week

  private[this] val numericDayOfWeek = P(CharIn('0' to '6').!)
    .map(value => ConstNode[DayOfWeek](value.toInt))
  private[this] val textualDayOfWeek = P(StringIn(DaysOfWeek.textValues: _*).!).map { value =>
    val index = DaysOfWeek.textValues.indexOf(value)
    ConstNode[DayOfWeek](index, Some(value))
  }
  val daysOfWeek: Parser[ConstNode[DayOfWeek]] = numericDayOfWeek | textualDayOfWeek

  //----------------------------------------
  // Field-Based Expression Atoms
  //----------------------------------------

  def each[F <: CronField](implicit unit: CronUnit[F]): Parser[EachNode[F]] =
    P("*").map(_ => EachNode[F])

  def between[F <: CronField](p: Parser[ConstNode[F]])(implicit unit: CronUnit[F]): Parser[BetweenNode[F]] =
    (p ~ "-" ~ p).map { case (min, max) => BetweenNode[F](min, max) }

  def several[F <: CronField](p: Parser[ConstNode[F]])(implicit unit: CronUnit[F]): Parser[SeveralNode[F]] = {
    def compose(p: Parser[EnumerableNode[F]])(implicit unit: CronUnit[F]): Parser[SeveralNode[F]] =
      p.rep(min = 2, sep = ",")
        .map(values => SeveralNode[F](NonEmptyList(values.head, values.tail: _*)))

    compose(between(p).map(Coproduct[EnumerableNode[F]](_)) | p.map(Coproduct[EnumerableNode[F]](_)))
  }

  def every[F <: CronField](p: Parser[ConstNode[F]])(implicit unit: CronUnit[F]): Parser[EveryNode[F]] = {
    def compose(p: Parser[DivisibleNode[F]])(implicit unit: CronUnit[F]): Parser[EveryNode[F]] =
      (p ~ "/" ~/ digit.rep(1).!).map { case (base, freq) => EveryNode[F](base, freq.toInt) }

    compose(
      several(p).map(Coproduct[DivisibleNode[F]](_)) |
      between(p).map(Coproduct[DivisibleNode[F]](_)) |
      each[F].map(Coproduct[DivisibleNode[F]](_))
    )
  }

  //----------------------------------------
  // AST Parsing & Building
  //----------------------------------------

  def of[F <: CronField](p: Parser[ConstNode[F]])(implicit unit: CronUnit[F]): Parser[FieldNode[F]] = {
    every(p).map(Coproduct[FieldNode[F]](_)) |
      several(p).map(Coproduct[FieldNode[F]](_)) |
      between(p).map(Coproduct[FieldNode[F]](_)) |
      p.map(Coproduct[FieldNode[F]](_)) |
      each[F].map(Coproduct[FieldNode[F]](_))
  }

  val cron: Parser[CronExprAST] = P(
    Start ~ of(seconds) ~ " " ~/ of(minutes) ~ " " ~/ of(hours) ~ " " ~/
      of(daysOfMonth) ~ " " ~/ of(months) ~ " " ~/ of(daysOfWeek) ~ End
  ).map { case (sec, min, hour, day, month, weekDay) =>
    sec :: min :: hour :: day :: month :: weekDay :: HNil
  }

}
