package cron4s.expr

import cron4s.{ArbitraryCronUnits, CronField, CronUnit, IsCronUnit}
import cron4s.testkit.discipline.IsFieldExprTests
import org.scalacheck._
import org.scalatest.{FunSuite, Matchers}
import org.typelevel.discipline.scalatest.Discipline

/**
  * Created by alonsodomin on 31/07/2016.
  */
class AnyExprSpec extends FunSuite with Discipline with ArbitraryExprs {
  import CronField._

  implicit lazy val arbitraryAnyMinuteExpr = Arbitrary(anyExprGen(CronUnit[Minute.type]))
  implicit lazy val arbitraryAnyHourExpr = Arbitrary(anyExprGen(CronUnit[Hour.type]))
  implicit lazy val arbitraryAnyDayOfMonthExpr = Arbitrary(anyExprGen(CronUnit[DayOfMonth.type]))
  implicit lazy val arbitraryAnyMonthExpr = Arbitrary(anyExprGen(CronUnit[Month.type]))
  implicit lazy val arbitraryAnyDayOfWeekExpr = Arbitrary(anyExprGen(CronUnit[DayOfWeek.type]))

  checkAll("AnyExpr[Minute]", IsFieldExprTests[AnyExpr, Minute.type].fieldExpr)
  checkAll("AnyExpr[Hour]", IsFieldExprTests[AnyExpr, Hour.type].fieldExpr)
  checkAll("AnyExpr[DayOfMonth]", IsFieldExprTests[AnyExpr, DayOfMonth.type].fieldExpr)
  checkAll("AnyExpr[Month]", IsFieldExprTests[AnyExpr, Month.type].fieldExpr)
  checkAll("AnyExpr[DayOfWeek]", IsFieldExprTests[AnyExpr, DayOfWeek.type].fieldExpr)

  /*property("min should be the same as its unit") = forAll(anyExpressions) {
    expr => expr.min == expr.unit.min
  }

  property("max should be the same as its unit") = forAll(anyExpressions) {
    expr => expr.max == expr.unit.max
  }

  property("range must be the same as its unit") = forAll(anyExpressions) {
    expr => expr.range == expr.unit.range
  }

  val valuesInsideRange = for {
    expr <- anyExpressions
    value <- Gen.choose(expr.min, expr.max)
  } yield (expr, value)

  property("match should always succeed inside units range") = forAll(valuesInsideRange) {
    case (expr, value) => expr.matches(value)
  }

  val valuesOutsideRange = for {
    expr <- anyExpressions
    value <- arbitrary[Int] if value < expr.min || value > expr.max
  } yield (expr, value)

  property("match should always fail outside its units range") = forAll(valuesOutsideRange) {
    case (expr, value) => !expr.matches(value)
  }

  val valuesAndSteps = for {
    expr  <- anyExpressions
    value <- arbitrary[Int]
    step  <- arbitrary[Int]
  } yield (expr, value, step)

  property("step should be the same as its unit") = forAll(valuesAndSteps) {
    case (expr, value, step) =>
      expr.step(value, step) == expr.unit.step(value, step)
  }*/

}
