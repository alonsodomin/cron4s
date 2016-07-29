package cron4s.parser

import cron4s.{CronField, CronUnit}
import cron4s.expr._

import scala.util.parsing.combinator.RegexParsers

/**
  * Created by alonsodomin on 01/01/2016.
  */
trait ExprParsers extends RegexParsers {
  import CronField._
  import CronUnit._
  import Expr._

  // Unit parsers

  def minute: Parser[ConstExpr[Minute.type]] =
    """[0-5]?\d""".r ^^ { value => ConstExpr(Minute, value.toInt) }

  def hour: Parser[ConstExpr[Hour.type]] =
    """2[0-3]|[01]?[0-9]""".r ^^ { value => ConstExpr(Hour, value.toInt) }

  def dayOfMonth: Parser[ConstExpr[DayOfMonth.type]] =
    """3[01]|[012]?\d""".r ^^ { value => ConstExpr(DayOfMonth, value.toInt) }

  private[this] def numericMonth: Parser[ConstExpr[Month.type]] =
    """1[0-2]|[1-9]""".r ^^ { value => ConstExpr(Month, value.toInt) }

  private[this] def textMonth: Parser[ConstExpr[Month.type]] =
    MonthsUnit.namedValues.mkString("|").r ^^ { v =>
      val value = MonthsUnit.namedValues.indexOf(v) + 1
      ConstExpr(Month, value, Some(v))
    }

  def month: Parser[ConstExpr[Month.type]] = numericMonth | textMonth

  private[this] def numberDayOfWeek: Parser[ConstExpr[DayOfWeek.type]] =
    """[0-6]""".r ^^ { value => ConstExpr(DayOfWeek, value.toInt) }

  private[this] def textDayOfWeek: Parser[ConstExpr[DayOfWeek.type]] =
    DaysOfWeekUnit.namedValues.mkString("|").r ^^ { v =>
      val value = DaysOfWeekUnit.namedValues.indexOf(v)
      ConstExpr(DayOfWeek, value, Some(v))
    }

  def dayOfWeek: Parser[ConstExpr[DayOfWeek.type]] = numberDayOfWeek | textDayOfWeek

  // Part parsers

  def always[F <: CronField](implicit unit: CronUnit[F]): Parser[AlwaysExpr[F]] =
    """\*""".r ^^ { _ => AlwaysExpr[F]() }

  def between[F <: CronField](p: Parser[ConstExpr[F]])
      (implicit unit: CronUnit[F]): Parser[BetweenExpr[F]] =
    p ~ ("-" ~> p) ^^ { case min ~ max => BetweenExpr(min, max) }

  def several[F <: CronField](p: Parser[EnumerableExpr[F]])
      (implicit unit: CronUnit[F]): Parser[SeveralExpr[F]] =
    p ~ (("," ~> p)+) ^^ { case head ~ tail => SeveralExpr((head :: tail).to[Vector]) }

  def every[F <: CronField](p: Parser[DivisibleExpr[F]])
      (implicit unit: CronUnit[F]): Parser[EveryExpr[F]] =
    p ~ """\/\d+""".r ^^ { case base ~ step => EveryExpr(base, step.substring(1).toInt) }

}