package cron4s.expr

import cron4s.CronField
import cron4s.laws.discipline.IsFieldExprTests
import cron4s.testkit.gen.ArbitraryConstExpr
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

/**
  * Created by alonsodomin on 31/07/2016.
  */
class ConstExprSpec extends FunSuite with Discipline with ArbitraryConstExpr {
  import CronField._

  checkAll("ConstExpr[Minute]", IsFieldExprTests[ConstExpr, Minute.type].fieldExpr)
  checkAll("ConstExpr[Hour]", IsFieldExprTests[ConstExpr, Hour.type].fieldExpr)
  checkAll("ConstExpr[DayOfMonth]", IsFieldExprTests[ConstExpr, DayOfMonth.type].fieldExpr)
  checkAll("ConstExpr[Month]", IsFieldExprTests[ConstExpr, Month.type].fieldExpr)
  checkAll("ConstExpr[DayOfWeek]", IsFieldExprTests[ConstExpr, DayOfWeek.type].fieldExpr)

  /*property("min should be the constant value") = forAll(constExpressions) {
    expr => expr.min == expr.value
  }

  property("max should be the constant value") = forAll(constExpressions) {
    expr => expr.max == expr.value
  }

  property("min and max should be equal") = forAll(constExpressions) {
    expr => expr.min == expr.max
  }

  property("range must be the single constant") = forAll(constExpressions) {
    expr => expr.range == IndexedSeq(expr.value)
  }

  property("match should always pass for the same value") = forAll(constExpressions) {
    expr => expr.matches(expr.value)
  }

  val nonConstValues = for {
    expr         <- constExpressions
    valueToMatch <- arbitrary[Int] if valueToMatch != expr.value
  } yield (expr, valueToMatch)

  property("match should pass only for the const value") = forAll(nonConstValues) {
    case (expr, valueToMatch) => !expr.matches(valueToMatch)
  }

  val stepsFromOutsideRange = for {
    expr      <- constExpressions
    fromValue <- arbitrary[Int] if !expr.unit.range.contains(fromValue)
    stepSize  <- arbitrary[Int]
  } yield (expr, fromValue, stepSize)

  property("stepping from outside the range should return none") = forAll(stepsFromOutsideRange) {
    case (expr, fromValue, stepSize) => expr.step(fromValue, stepSize).isEmpty
  }

  val stepsFromInsideRange = for {
    expr      <- constExpressions
    fromValue <- Gen.choose(expr.unit.min, expr.unit.max)
    stepSize  <- arbitrary[Int]
  } yield (expr, fromValue, stepSize)

  property("stepping with a zero step size returns const") = forAll(stepsFromInsideRange) {
    case (expr, fromValue, _) =>
      expr.step(fromValue, 0).contains((expr.value, 0))
  }

  property("stepping from value previous to const returns same const consuming one step") = forAll(stepsFromInsideRange) {
    case (expr, fromValue, stepSize) =>
      (fromValue < expr.value && stepSize != 0) ==> expr.step(fromValue, stepSize).contains((expr.value, stepSize - 1))
  }

  property("stepping from value after const returns same const without consuming steps") = forAll(stepsFromInsideRange) {
    case (expr, fromValue, stepSize) =>
      (fromValue >= expr.value && stepSize != 0) ==> expr.step(fromValue, stepSize).contains((expr.value, stepSize))
  }*/

}
