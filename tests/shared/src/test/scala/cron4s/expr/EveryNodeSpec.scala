package cron4s.expr

import cron4s.CronField
import cron4s.testkit.discipline.ExprTests
import cron4s.testkit.gen.ArbitraryEveryNode

import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

/**
  * Created by alonsodomin on 01/08/2016.
  */
class EveryNodeSpec extends FunSuite with Discipline with ArbitraryEveryNode {
  import CronField._

  checkAll("Every[Second]", ExprTests[EveryNode, Second].expr)
  checkAll("Every[Minute]", ExprTests[EveryNode, Minute].expr)
  checkAll("Every[Hour]", ExprTests[EveryNode, Hour].expr)
  checkAll("Every[DayOfMonth]", ExprTests[EveryNode, DayOfMonth].expr)
  checkAll("Every[Month]", ExprTests[EveryNode, Month].expr)
  checkAll("Every[DayOfWeek]", ExprTests[EveryNode, DayOfWeek].expr)

}
