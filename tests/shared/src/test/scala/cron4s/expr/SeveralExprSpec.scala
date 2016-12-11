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

  checkAll("SeveralExpr[Second]", IsFieldExprTests[SeveralExpr, Second].fieldExpr)
  checkAll("SeveralExpr[Minute]", IsFieldExprTests[SeveralExpr, Minute].fieldExpr)
  checkAll("SeveralExpr[Hour]", IsFieldExprTests[SeveralExpr, Hour].fieldExpr)
  checkAll("SeveralExpr[DayOfMonth]", IsFieldExprTests[SeveralExpr, DayOfMonth].fieldExpr)
  checkAll("SeveralExpr[Month]", IsFieldExprTests[SeveralExpr, Month].fieldExpr)
  checkAll("SeveralExpr[DayOfWeek]", IsFieldExprTests[SeveralExpr, DayOfWeek].fieldExpr)

}
