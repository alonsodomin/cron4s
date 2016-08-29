package cron4s.expr

import cron4s.CronField
import cron4s.testkit.discipline.IsFieldExprTests
import cron4s.testkit.gen.ArbitraryEveryExpr
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

/**
  * Created by alonsodomin on 01/08/2016.
  */
class EveryExprSpec extends FunSuite with Discipline with ArbitraryEveryExpr {
  import CronField._

  checkAll("EveryExpr[Second]", IsFieldExprTests[EveryExpr, Second.type].fieldExpr)
  checkAll("EveryExpr[Minute]", IsFieldExprTests[EveryExpr, Minute.type].fieldExpr)
  checkAll("EveryExpr[Hour]", IsFieldExprTests[EveryExpr, Hour.type].fieldExpr)
  checkAll("EveryExpr[DayOfMonth]", IsFieldExprTests[EveryExpr, DayOfMonth.type].fieldExpr)
  checkAll("EveryExpr[Month]", IsFieldExprTests[EveryExpr, Month.type].fieldExpr)
  checkAll("EveryExpr[DayOfWeek]", IsFieldExprTests[EveryExpr, DayOfWeek.type].fieldExpr)

}
