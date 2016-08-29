package cron4s.testkit.gen

import cron4s.CronField._
import cron4s.CronUnit
import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryEveryExpr extends ExprGens {

  implicit lazy val arbitraryEverySecondExpr = Arbitrary(everyExprGen(CronUnit[Second.type]))
  implicit lazy val arbitraryEveryMinuteExpr = Arbitrary(everyExprGen(CronUnit[Minute.type]))
  implicit lazy val arbitraryEveryHourExpr = Arbitrary(everyExprGen(CronUnit[Hour.type]))
  implicit lazy val arbitraryEveryDayOfMonthExpr = Arbitrary(everyExprGen(CronUnit[DayOfMonth.type]))
  implicit lazy val arbitraryEveryMonthExpr = Arbitrary(everyExprGen(CronUnit[Month.type]))
  implicit lazy val arbitraryEveryDayOfWeekExpr = Arbitrary(everyExprGen(CronUnit[DayOfWeek.type]))

}
