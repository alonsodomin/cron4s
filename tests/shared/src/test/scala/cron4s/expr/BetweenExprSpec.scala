package cron4s.expr

import cron4s.CronField._
import cron4s.testkit.discipline.IsFieldExprTests
import cron4s.testkit.gen.ArbitraryBetweenExpr
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

/**
  * Created by alonsodomin on 31/07/2016.
  */
class BetweenExprSpec extends FunSuite with Discipline with ArbitraryBetweenExpr {

  checkAll("BetweenExpr[Second]", IsFieldExprTests[BetweenExpr, Second.type].fieldExpr)
  checkAll("BetweenExpr[Minute]", IsFieldExprTests[BetweenExpr, Minute.type].fieldExpr)
  checkAll("BetweenExpr[Hour]", IsFieldExprTests[BetweenExpr, Hour.type].fieldExpr)
  checkAll("BetweenExpr[DayOfMonth]", IsFieldExprTests[BetweenExpr, DayOfMonth.type].fieldExpr)
  checkAll("BetweenExpr[Month]", IsFieldExprTests[BetweenExpr, Month.type].fieldExpr)
  checkAll("BetweenExpr[DayOfWeek]", IsFieldExprTests[BetweenExpr, DayOfWeek.type].fieldExpr)

}
