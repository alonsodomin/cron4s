package cron4s.expr

import cron4s.CronField._
import cron4s.testkit.discipline.ExprTests
import cron4s.testkit.gen.ArbitrarySeveralNode

import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline


/**
  * Created by alonsodomin on 01/08/2016.
  */
class SeveralNodeSpec extends FunSuite with Discipline with ArbitrarySeveralNode {

  checkAll("Several[Second]", ExprTests[SeveralNode, Second].expr)
  checkAll("Several[Minute]", ExprTests[SeveralNode, Minute].expr)
  checkAll("Several[Hour]", ExprTests[SeveralNode, Hour].expr)
  checkAll("Several[DayOfMonth]", ExprTests[SeveralNode, DayOfMonth].expr)
  checkAll("Several[Month]", ExprTests[SeveralNode, Month].expr)
  checkAll("Several[DayOfWeek]", ExprTests[SeveralNode, DayOfWeek].expr)

}
