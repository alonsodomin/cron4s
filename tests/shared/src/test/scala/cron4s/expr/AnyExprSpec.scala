package cron4s.expr

import cron4s.CronField
import cron4s.testkit.discipline.IsFieldExprTests
import cron4s.testkit.gen.ArbitraryAnyExpr
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

/**
  * Created by alonsodomin on 31/07/2016.
  */
class AnyExprSpec extends FunSuite with Discipline with ArbitraryAnyExpr {
  import CronField._

  checkAll("AnyExpr[Second]", IsFieldExprTests[AnyExpr, Second.type].fieldExpr)
  checkAll("AnyExpr[Minute]", IsFieldExprTests[AnyExpr, Minute.type].fieldExpr)
  checkAll("AnyExpr[Hour]", IsFieldExprTests[AnyExpr, Hour.type].fieldExpr)
  checkAll("AnyExpr[DayOfMonth]", IsFieldExprTests[AnyExpr, DayOfMonth.type].fieldExpr)
  checkAll("AnyExpr[Month]", IsFieldExprTests[AnyExpr, Month.type].fieldExpr)
  checkAll("AnyExpr[DayOfWeek]", IsFieldExprTests[AnyExpr, DayOfWeek.type].fieldExpr)

}
