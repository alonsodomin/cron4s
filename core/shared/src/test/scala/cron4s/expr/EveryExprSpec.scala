package cron4s.expr

import org.scalacheck._

/**
  * Created by alonsodomin on 01/08/2016.
  */
object EveryExprSpec extends Properties("EveryExpr") with ExprGenerators {
  import Prop._
  import Arbitrary.arbitrary

  property("min should be equal to its base min") = forAll(everyExpressions) {
    expr => expr.min == expr.value.min
  }

  property("max should be equal to its base max") = forAll(everyExpressions) {
    expr => expr.max == expr.value.max
  }

}
