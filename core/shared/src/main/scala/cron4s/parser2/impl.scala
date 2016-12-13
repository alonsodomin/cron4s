package cron4s.parser2

import cron4s.{CronField, CronUnit}
import cron4s.expr._

import fastparse.all._

/**
  * Created by alonsodomin on 11/12/2016.
  */
object impl {
  import dsl._

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
