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

  /*property("range must be stepped progression over its base") = forAll(everyExpressions) {
    expr =>
      val elems = Stream.iterate[Option[(Int, Int)]](Some(expr.min, 0)) {
        prev => prev.flatMap { case (v, _) =>
          println(s"stepping from $v at test")
          expr.value.step(v, expr.freq)
        }
      }.flatten.takeWhile(_._2 < 1).map(_._1)

      expr.range == elems.toIndexedSeq
  }*/

}
