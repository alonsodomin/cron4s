package cron4s.parser

import cron4s.expr._

import scala.util.parsing.combinator.RegexParsers

/**
  * Created by alonsodomin on 01/01/2016.
  */
trait PartParsers extends RegexParsers {
  import CronField._
  import CronUnit._

  def minute: Parser[Scalar[Minute.type]] =
    """[0-5]?\d""".r ^^ { value => Scalar(Minute, value.toInt) }

  def hour: Parser[Scalar[Hour.type]] =
    """2[0-3]|[01]?[0-9]""".r ^^ { value => Scalar(Hour, value.toInt) }

  def dayOfMonth: Parser[Scalar[DayOfMonth.type]] =
    """3[01]|[012]?\d""".r ^^ { value => Scalar(DayOfMonth, value.toInt) }

  private[this] def numericMonth: Parser[Scalar[Month.type]] =
    """1[0-2]|[1-9]""".r ^^ { value => Scalar(Month, value.toInt) }

  private[this] def textMonth: Parser[Scalar[Month.type]] =
    MonthsUnit.namedValues.mkString("|").r ^^ { v =>
      val value = MonthsUnit.namedValues.indexOf(v) + 1
      Scalar(Month, value, Some(v)) 
    }

  def month: Parser[Scalar[Month.type]] = numericMonth | textMonth

  private[this] def numberDayOfWeek: Parser[Scalar[DayOfWeek.type]] =
    """[0-6]""".r ^^ { value => Scalar(DayOfWeek, value.toInt) }

  private[this] def textDayOfWeek: Parser[Scalar[DayOfWeek.type]] =
    DaysOfWeekUnit.namedValues.mkString("|").r ^^ { v =>
      val value = DaysOfWeekUnit.namedValues.indexOf(v)
      Scalar(DayOfWeek, value, Some(v)) 
    }

  def dayOfWeek: Parser[Scalar[DayOfWeek.type]] = numberDayOfWeek | textDayOfWeek

  def always[F <: CronField](implicit unit: CronUnit[F]): Parser[Always[F]] =
    """\*""".r ^^ { _ => Always[F]() }

  def between[F <: CronField](p: Parser[Scalar[F]])(implicit unit: CronUnit[F]): Parser[Between[F]] =
    p ~ ("-" ~> p) ^^ { case min ~ max => Between(min, max) }

  def several[F <: CronField](p: Parser[EnumerablePart[F]])(implicit unit: CronUnit[F]): Parser[Several[F]] =
    p ~ (("," ~> p)+) ^^ { case head ~ tail => Several((head :: tail).to[Vector]) }

  def every[F <: CronField](p: Parser[DivisiblePart[F]])(implicit unit: CronUnit[F]): Parser[Every[F]] =
    p ~ """\/\d+""".r ^^ { case base ~ step => Every(base, step.substring(1).toInt) }

}