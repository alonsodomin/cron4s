package cron4s.expr

import cron4s.CronField._
import cron4s.testkit.discipline.ExprTests
import cron4s.testkit.gen.ArbitraryBetweenNode

import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

/**
  * Created by alonsodomin on 31/07/2016.
  */
class BetweenSpec extends FunSuite with Discipline with ArbitraryBetweenNode {

  checkAll("Between[Second]", ExprTests[BetweenNode, Second].expr)
  checkAll("Between[Minute]", ExprTests[BetweenNode, Minute].expr)
  checkAll("Between[Hour]", ExprTests[BetweenNode, Hour].expr)
  checkAll("Between[DayOfMonth]", ExprTests[BetweenNode, DayOfMonth].expr)
  checkAll("Between[Month]", ExprTests[BetweenNode, Month].expr)
  checkAll("Between[DayOfWeek]", ExprTests[BetweenNode, DayOfWeek].expr)

}
