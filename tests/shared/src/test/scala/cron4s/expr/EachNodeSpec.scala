package cron4s.expr

import cron4s.CronField
import cron4s.testkit.discipline.ExprTests
import cron4s.testkit.gen.ArbitraryEachNode

import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

/**
  * Created by alonsodomin on 31/07/2016.
  */
class EachNodeSpec extends FunSuite with Discipline with ArbitraryEachNode {
  import CronField._

  checkAll("Each[Second]", ExprTests[EachNode, Second].expr)
  checkAll("Each[Minute]", ExprTests[EachNode, Minute].expr)
  checkAll("Each[Hour]", ExprTests[EachNode, Hour].expr)
  checkAll("Each[DayOfMonth]", ExprTests[EachNode, DayOfMonth].expr)
  checkAll("Each[Month]", ExprTests[EachNode, Month].expr)
  checkAll("Each[DayOfWeek]", ExprTests[EachNode, DayOfWeek].expr)

}
