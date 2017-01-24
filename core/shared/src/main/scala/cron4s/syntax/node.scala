package cron4s.syntax

import cron4s.CronField
import cron4s.spi.DateTimeNode
import cron4s.types.Predicate

private[syntax] class DateTimeNodeOps[E[_ <: CronField], F <: CronField, DateTime](self: E[F], tc: DateTimeNode[E, F, DateTime]) {

  def matchesIn: Predicate[DateTime] = tc.matchesIn(self)

  def nextIn(dateTime: DateTime): Option[DateTime] = tc.nextIn(self)(dateTime)

  def prevIn(dateTime: DateTime): Option[DateTime] = tc.prevIn(self)(dateTime)

  def stepIn(dateTime: DateTime, step: Int): Option[DateTime] = tc.stepIn(self)(dateTime, step)

}

private[syntax] trait DateTimeNodeSyntax {

  implicit def toDateTimeNodeOps[E[_ <: CronField], F <: CronField, DateTime]
      (target: E[F])
      (implicit tc0: DateTimeNode[E, F, DateTime]): DateTimeNodeOps[E, F, DateTime] =
    new DateTimeNodeOps[E, F, DateTime](target, tc0)

}

object node extends DateTimeNodeSyntax