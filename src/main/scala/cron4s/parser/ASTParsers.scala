package cron4s.parser

import cron4s.expr._

import scala.util.parsing.combinator.RegexParsers

/**
  * Created by alonsodomin on 02/01/2016.
  */

trait ASTParsers extends PartParsers {
  import CronField._

  private[this] abstract class ParserFactory[F <: CronField] {
    def build(p: Parser[Scalar[F]])(implicit unit: CronUnit[F]): Parser[Part[F]]
  }

  private[this] class DefaultParserFactory[F <: CronField] extends ParserFactory[F] {

    private[this] def everyAST(p: Parser[Scalar[F]])
        (implicit unit: CronUnit[F]): Parser[Every[F]] =
      every(several(between(p) | p) | between(p) | always)

    private[this] def severalAST(p: Parser[Scalar[F]])
        (implicit unit: CronUnit[F]): Parser[Several[F]] =
      several(between(p) | p)

    def build(p: Parser[Scalar[F]])(implicit unit: CronUnit[F]): Parser[Part[F]] =
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
