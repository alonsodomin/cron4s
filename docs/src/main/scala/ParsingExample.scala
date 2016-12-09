
object ParsingExample {

  // #parse_example
  import cron4s._
  import cron4s.expr.CronExpr

  val parsedCron: Either[ParseError, CronExpr] = Cron("10-35 2,4,6 * * *")
  // #parse_example

}
