package cron4s.parser

import cron4s.expr._

import scala.util.parsing.combinator.RegexParsers

/**
  * Created by alonsodomin on 01/01/2016.
  */
trait PartParsers extends RegexParsers {
  import CronField._
  import CronUnit._

  def minute: Parser[Scalar[Int, Minute.type]] =
    """[0-5]?\d""".r ^^ { value => Scalar(Minute, value.toInt) }

  def hour: Parser[Scalar[Int, Hour.type]] =
    """2[0-3]|[01]?[0-9]""".r ^^ { value => Scalar(Hour, value.toInt) }

  def dayOfMonth: Parser[Scalar[Int, DayOfMonth.type]] =
    """3[01]|[012]?\d""".r ^^ { value => Scalar(DayOfMonth, value.toInt) }

  def numericMonth: Parser[Scalar[Int, Month.type]] =
    """1[0-2]|[1-9]""".r ^^ { value => Scalar(Month, value.toInt) }

  def textMonth: Parser[Scalar[String, Month.type]] =
    TextMonths.values.mkString("|").r ^^ { value => Scalar(Month, value) }

  def month[V: Value]: Parser[Scalar[V, Month.type]] =
    numericMonth.asInstanceOf[Parser[Scalar[V, Month.type]]] | textMonth.asInstanceOf[Parser[Scalar[V, Month.type]]]

  private[this] def numberDayOfWeek: Parser[Scalar[Int, DayOfWeek.type]] =
    """[0-6]""".r ^^ { value => Scalar(DayOfWeek, value.toInt) }

  private[this] def textDayOfWeek: Parser[Scalar[String, DayOfWeek.type]] =
    TextDaysOfWeek.values.mkString("|").r ^^ { value => Scalar(DayOfWeek, value) }

  def dayOfWeek[V: Value]: Parser[Scalar[V, DayOfWeek.type]] =
    numberDayOfWeek.asInstanceOf[Parser[Scalar[V, DayOfWeek.type]]] | textDayOfWeek.asInstanceOf[Parser[Scalar[V, DayOfWeek.type]]]

  def always[V: Value, U <: CronField](implicit unitOps: CronUnit[V, U]): Parser[Always[V, U]] =
    """\*""".r ^^ { _ => Always[V, U]() }

  def between[V: Value, U <: CronField](p: Parser[Scalar[V, U]])(implicit unitOps: CronUnit[V, U]): Parser[Between[V, U]] =
    p ~ ("-" ~> p) ^^ { case min ~ max => Between(min, max) }

  def several[V: Value, U <: CronField](p: Parser[EnumerablePart[V, U]])(implicit unitOps: CronUnit[V, U]): Parser[Several[V, U]] =
    p ~ (("," ~> p)+) ^^ { case head ~ tail => Several((head :: tail).to[Vector]) }

  def every[V: Value, U <: CronField](p: Parser[DivisiblePart[V, U]])(implicit unitOps: CronUnit[V, U]): Parser[Every[V, U]] =
    p ~ """\/\d+""".r ^^ { case base ~ step => Every(base, step.substring(1).toInt) }

}