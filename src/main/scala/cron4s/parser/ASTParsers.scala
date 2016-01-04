package cron4s.parser

import cron4s.expr._

import scala.util.parsing.combinator.RegexParsers

/**
  * Created by alonsodomin on 02/01/2016.
  */

trait ASTParsers extends PartParsers {
  import CronField._

  private[this] abstract class ParserFactory[U <: CronField] {
    def build(p: Parser[Scalar[U]])(implicit unit: CronUnit[U]): Parser[Part[U]]
  }

  private[this] class DefaultParserFactory[U <: CronField] extends ParserFactory[U] {

    private[this] def everyAST(p: Parser[Scalar[U]])(implicit unit: CronUnit[U]): Parser[Every[U]] =
      every(several(between(p) | p) | between(p) | always)

    private[this] def severalAST(p: Parser[Scalar[U]])(implicit unit: CronUnit[U]): Parser[Several[U]] =
      several(between(p) | p)

    def build(p: Parser[Scalar[U]])(implicit unit: CronUnit[U]): Parser[Part[U]] =
      everyAST(p) | severalAST(p) | between(p) | p | always

  }

  val minutes = new DefaultParserFactory[Minute.type].build(minute)
  val hours = new DefaultParserFactory[Hour.type].build(hour)
  val dayOfMonths = new DefaultParserFactory[DayOfMonth.type].build(dayOfMonth)

  val months = new DefaultParserFactory[Month.type].build(month)

  val daysOfWeek = new DefaultParserFactory[DayOfWeek.type].build(dayOfWeek)

  def cron: Parser[CronExpr] = minutes ~ hours ~ dayOfMonths ~ months ~ daysOfWeek ^^ {
    case m ~ h ~ dm ~ mm ~ dw => CronExpr(m, h, dm, mm, dw)
  }

}
