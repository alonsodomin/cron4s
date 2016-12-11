package cron4s.expr

import cron4s.CronField
import cron4s.testkit.discipline.IsFieldExprTests
import cron4s.testkit.gen.ArbitraryConstExpr
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

/**
  * Created by alonsodomin on 31/07/2016.
  */
class ConstExprSpec extends FunSuite with Discipline with ArbitraryConstExpr {
  import CronField._

  checkAll("ConstExpr[Second]", IsFieldExprTests[ConstExpr, Second].fieldExpr)
  checkAll("ConstExpr[Minute]", IsFieldExprTests[ConstExpr, Minute].fieldExpr)
  checkAll("ConstExpr[Hour]", IsFieldExprTests[ConstExpr, Hour].fieldExpr)
  checkAll("ConstExpr[DayOfMonth]", IsFieldExprTests[ConstExpr, DayOfMonth].fieldExpr)
  checkAll("ConstExpr[Month]", IsFieldExprTests[ConstExpr, Month].fieldExpr)
  checkAll("ConstExpr[DayOfWeek]", IsFieldExprTests[ConstExpr, DayOfWeek].fieldExpr)

}
