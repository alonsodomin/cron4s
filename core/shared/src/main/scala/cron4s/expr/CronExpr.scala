package cron4s.expr

/**
  * Created by domingueza on 29/07/2016.
  */
final case class CronExpr(repr: CronExprRepr) {

  lazy val minutes = repr.head
  lazy val hours = repr.tail.head
  lazy val daysOfMonth = repr.tail.tail.head
  lazy val months = repr.tail.tail.tail.head
  lazy val daysOfWeek = repr.tail.tail.tail.tail.head

}
