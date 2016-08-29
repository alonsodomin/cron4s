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

  checkAll("ConstExpr[Second]", IsFieldExprTests[ConstExpr, Second.type].fieldExpr)
  checkAll("ConstExpr[Minute]", IsFieldExprTests[ConstExpr, Minute.type].fieldExpr)
  checkAll("ConstExpr[Hour]", IsFieldExprTests[ConstExpr, Hour.type].fieldExpr)
  checkAll("ConstExpr[DayOfMonth]", IsFieldExprTests[ConstExpr, DayOfMonth.type].fieldExpr)
  checkAll("ConstExpr[Month]", IsFieldExprTests[ConstExpr, Month.type].fieldExpr)
  checkAll("ConstExpr[DayOfWeek]", IsFieldExprTests[ConstExpr, DayOfWeek.type].fieldExpr)

}
