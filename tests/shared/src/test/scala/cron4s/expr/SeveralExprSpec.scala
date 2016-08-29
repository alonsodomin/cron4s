package cron4s.expr

import cron4s.CronField._
import cron4s.testkit.discipline.IsFieldExprTests
import cron4s.testkit.gen.ArbitrarySeveralExpr
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline


/**
  * Created by alonsodomin on 01/08/2016.
  */
class SeveralExprSpec extends FunSuite with Discipline with ArbitrarySeveralExpr {

  checkAll("SeveralExpr[Second]", IsFieldExprTests[SeveralExpr, Second.type].fieldExpr)
  checkAll("SeveralExpr[Minute]", IsFieldExprTests[SeveralExpr, Minute.type].fieldExpr)
  checkAll("SeveralExpr[Hour]", IsFieldExprTests[SeveralExpr, Hour.type].fieldExpr)
  checkAll("SeveralExpr[DayOfMonth]", IsFieldExprTests[SeveralExpr, DayOfMonth.type].fieldExpr)
  checkAll("SeveralExpr[Month]", IsFieldExprTests[SeveralExpr, Month.type].fieldExpr)
  checkAll("SeveralExpr[DayOfWeek]", IsFieldExprTests[SeveralExpr, DayOfWeek.type].fieldExpr)

}
