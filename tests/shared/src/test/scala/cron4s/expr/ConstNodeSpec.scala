package cron4s.expr

import cron4s.CronField
import cron4s.testkit.discipline.ExprTests
import cron4s.testkit.gen.ArbitraryConstNode

import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

/**
  * Created by alonsodomin on 31/07/2016.
  */
class ConstNodeSpec extends FunSuite with Discipline with ArbitraryConstNode {
  import CronField._

  checkAll("Const[Second]", ExprTests[ConstNode, Second].expr)
  checkAll("Const[Minute]", ExprTests[ConstNode, Minute].expr)
  checkAll("Const[Hour]", ExprTests[ConstNode, Hour].expr)
  checkAll("Const[DayOfMonth]", ExprTests[ConstNode, DayOfMonth].expr)
  checkAll("Const[Month]", ExprTests[ConstNode, Month].expr)
  checkAll("Const[DayOfWeek]", ExprTests[ConstNode, DayOfWeek].expr)

}
