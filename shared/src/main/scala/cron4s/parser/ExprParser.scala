package cron4s.parser

import cron4s.expr._
import org.parboiled2._

/**
  * Created by alonsodomin on 21/07/2016.
  */
class ExprParser(val input: ParserInput) extends Parser {
  import CronField._
  import CronUnit._
  import Expr._

  def WS = rule { oneOrMore(' ') }

  /*implicit def wspStr(s: String): Rule0 = rule {
    str(s) ~ zeroOrMore(' ')
  }*/

  def Always[F <: CronField : CronUnit]: Rule1[AlwaysExpr[F]] = rule {
    capture('*') ~> ((_: String) => AlwaysExpr[F]())
  }

  def Between[F <: CronField : CronUnit](elem: Rule1[ConstExpr[F]]): Rule1[BetweenExpr[F]] = rule {
    (elem ~ '-' ~ elem) ~> ((min: ConstExpr[F], max: ConstExpr[F]) => BetweenExpr[F](min, max))
  }

  def Several[F <: CronField : CronUnit](elem: Rule1[EnumerableExpr[F]]): Rule1[SeveralExpr[F]] = rule {
    elem ~ oneOrMore(',' ~ elem) ~> {
      (head: EnumerableExpr[F], values: Seq[EnumerableExpr[F]]) =>
        SeveralExpr(values.toVector)
    }
  }

  def Every[F <: CronField : CronUnit](base: Rule1[DivisibleExpr[F]]): Rule1[EveryExpr[F]] = rule {
    (base ~ '/' ~ capture(CharPredicate.Digit)) ~> {
      (value: DivisibleExpr[F], freq: String) =>
        EveryExpr(value, freq.toInt)
    }
  }

  def MinuteConst = rule {
    capture(
      (CharPredicate('0' to '5') ~ CharPredicate.Digit) | CharPredicate.Digit
    ) ~> (value => ConstExpr(Minute, value.toInt))
  }

  def HourConst = rule {
    capture(
      ('2' ~ CharPredicate('0' to '3')) |
      (anyOf("01") ~ CharPredicate.Digit) |
      CharPredicate.Digit
    ) ~> (value => ConstExpr(Hour, value.toInt))
  }

  def DayOfMonthConst = rule {
    capture(
      ('3' ~ anyOf("01")) |
      (CharPredicate('0' to '2') ~ CharPredicate.Digit) |
      CharPredicate.Digit19
    ) ~> (value => ConstExpr(DayOfMonth, value.toInt))
  }

  def TextualMonthConst = rule {
    capture(
      ignoreCase("jan") | ignoreCase("feb") | ignoreCase("mar") | ignoreCase("apr") | ignoreCase("may") |
      ignoreCase("jun") | ignoreCase("jul") | ignoreCase("ago") | ignoreCase("sep") | ignoreCase("oct") |
      ignoreCase("nov") | ignoreCase("dec")
    ) ~> { value =>
      val numeric = MonthsUnit.namedValues.indexOf(value) + 1
      ConstExpr(Month, numeric, Some(value))
    }
  }

  def NumericMonthConst = rule {
    capture(
      ('1' ~ CharPredicate('0' to '2')) |
      CharPredicate.Digit19
    ) ~> (value => ConstExpr(Month, value.toInt))
  }

  def MonthConst = rule { TextualMonthConst | NumericMonthConst }

  def TextualDayOfWeekConst = rule {
    capture(
      ignoreCase("mon") | ignoreCase("tue") | ignoreCase("wed") | ignoreCase("thu") |
      ignoreCase("fri") | ignoreCase("sat") | ignoreCase("sun")
    ) ~> { value =>
      val numeric = DaysOfWeekUnit.namedValues.indexOf(value)
      ConstExpr(DayOfWeek, numeric, Some(value))
    }
  }

  def NumericDayOfWeekConst = rule {
    capture(CharPredicate('1' to '7')) ~> (value => ConstExpr(DayOfWeek, value.toInt))
  }

  def DayOfWeekConst = rule { TextualDayOfWeekConst | NumericDayOfWeekConst }

  private[this] def Part[F <: CronField : CronUnit](const: Rule1[ConstExpr[F]]): Rule1[Expr[F]] = {
    val between = Between(const)
    val several = Several(rule { between | const })
    val every = Every(rule { several | between | Always[F] })

    rule { every | several | between | const }
  }

  def MinutesPart = rule { Part(MinuteConst) }
  def HoursPart = rule { Part(HourConst) }
  def DaysOfMonthPart = rule { Part(DayOfMonthConst) }
  def MonthsPart = rule { Part(MonthConst) }
  def DaysOfWeekPart = rule { Part(DayOfWeekConst) }

  def InputExpr = rule {
    MinutesPart ~ WS ~ HoursPart ~ WS ~ DaysOfMonthPart ~ WS ~ MonthsPart ~ WS ~ DaysOfWeekPart ~ EOI ~> {
      (minutes, hours, daysOfMonth, months, daysOfWeek) =>
        CronExpr(minutes, hours, daysOfMonth, months, daysOfWeek)
    }
  }

  /*def InputExpr = rule {
    MinuteConst ~ HourConst ~ DayOfMonthConst ~ MonthConst ~ DayOfWeekConst ~ EOI ~> {
      (minutes, hours, daysOfMonth, months, daysOfWeek) =>
        CronExpr(minutes, hours, daysOfMonth, months, daysOfWeek)
    }
  }*/

}
