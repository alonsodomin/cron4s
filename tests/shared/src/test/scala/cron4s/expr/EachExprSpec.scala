package cron4s.expr

import cron4s.CronField
import cron4s.testkit.discipline.IsFieldExprTests
import cron4s.testkit.gen.ArbitraryEachExpr
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

/**
  * Created by alonsodomin on 31/07/2016.
  */
class EachExprSpec extends FunSuite with Discipline with ArbitraryEachExpr {
  import CronField._

  checkAll("EachExpr[Second]", IsFieldExprTests[EachExpr, Second].fieldExpr)
  checkAll("EachExpr[Minute]", IsFieldExprTests[EachExpr, Minute].fieldExpr)
  checkAll("EachExpr[Hour]", IsFieldExprTests[EachExpr, Hour].fieldExpr)
  checkAll("EachExpr[DayOfMonth]", IsFieldExprTests[EachExpr, DayOfMonth].fieldExpr)
  checkAll("EachExpr[Month]", IsFieldExprTests[EachExpr, Month].fieldExpr)
  checkAll("EachExpr[DayOfWeek]", IsFieldExprTests[EachExpr, DayOfWeek].fieldExpr)

}
