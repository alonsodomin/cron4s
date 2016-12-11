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

  checkAll("BetweenExpr[Second]", IsFieldExprTests[BetweenExpr, Second].fieldExpr)
  checkAll("BetweenExpr[Minute]", IsFieldExprTests[BetweenExpr, Minute].fieldExpr)
  checkAll("BetweenExpr[Hour]", IsFieldExprTests[BetweenExpr, Hour].fieldExpr)
  checkAll("BetweenExpr[DayOfMonth]", IsFieldExprTests[BetweenExpr, DayOfMonth].fieldExpr)
  checkAll("BetweenExpr[Month]", IsFieldExprTests[BetweenExpr, Month].fieldExpr)
  checkAll("BetweenExpr[DayOfWeek]", IsFieldExprTests[BetweenExpr, DayOfWeek].fieldExpr)

}
