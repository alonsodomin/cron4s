package cron4s.parser

import cron4s.expr._

import scala.util.parsing.combinator.RegexParsers

/**
  * Created by alonsodomin on 02/01/2016.
  */

trait ASTParsers extends PartParsers {
  import CronField._

  private[this] abstract class ParserFactory[V: Value, U <: CronField] {
    def build(p: Parser[Scalar[V, U]])(implicit unitOps: CronUnit[V, U]): Parser[Part[V, U]]
  }

  private[this] class DefaultParserFactory[V: Value, U <: CronField] extends ParserFactory[V, U] {

    private[this] def everyAST(p: Parser[Scalar[V, U]])(implicit unitOps: CronUnit[V, U]): Parser[Every[V, U]] =
      every(several(between(p) | p) | between(p) | always)

    private[this] def severalAST(p: Parser[Scalar[V, U]])(implicit unitOps: CronUnit[V, U]): Parser[Several[V, U]] =
      several(between(p) | p)

    def build(p: Parser[Scalar[V, U]])(implicit unitOps: CronUnit[V, U]): Parser[Part[V, U]] =
      everyAST(p) | severalAST(p) | between(p) | p | always

  }

  val minutes = new DefaultParserFactory[Int, Minute.type].build(minute)
  val hours = new DefaultParserFactory[Int, Hour.type].build(hour)
  val dayOfMonths = new DefaultParserFactory[Int, DayOfMonth.type].build(dayOfMonth)

  val numericMonths = new DefaultParserFactory[Int, Month.type].build(numericMonth)
  val textMonths = new DefaultParserFactory[String, Month.type].build(textMonth)
  val months = new DefaultParserFactory[Int, Month.type].build(month) | new DefaultParserFactory[String, Month.type].build(month)

  val daysOfWeek = new DefaultParserFactory[Int, DayOfWeek.type].build(dayOfWeek) | new DefaultParserFactory[String, DayOfWeek.type].build(dayOfWeek)

  def cron: Parser[CronExpr] = minutes ~ hours ~ dayOfMonths ~ months ~ daysOfWeek ^^ {
    case m ~ h ~ dm ~ mm ~ dw =>
      import Segment._
      val monthSegment = mm match {
        case p: Part[Int, Month.type] => new NumericMonths(p)
        case p: Part[String, Month.type] => new TextMonths(p)
      }
      val dayOfWeekSegment = dw match {
        case p: Part[Int, DayOfWeek.type] => new NumericDaysOfWeek(p)
        case p: Part[String, DayOfWeek.type] => new TextDaysOfWeek(p)
      }
      CronExpr(m, h, dm, monthSegment, dayOfWeekSegment)
  }

}
