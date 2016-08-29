package cron4s.types

import cron4s.CronField
import org.scalacheck._

/**
  * Created by alonsodomin on 04/08/2016.
  */
object HasCronFieldSpec extends Properties("HasCronField") {
  import Prop._
  import Arbitrary.arbitrary

  /*def hasCronField[F <: CronField]

  val sequentialGen = for {
    vector     <- Gen.nonEmptyContainerOf[Vector, Int](arbitrary[Int])
    sequential <- Gen.const(Sequential.sequential(vector.sorted))
  } yield sequential

  val sequentialAndValues = for {
    seq   <- sequentialGen
    value <- Gen.choose(seq.min, seq.max)
  } yield (seq, value)

  property("next") = forAll(sequentialAndValues) {
    case (seq, value) => seq.next(value) == seq.step(value, 1).map(_._1)
  }

  property("previous") = forAll(sequentialAndValues) {
    case (seq, value) => seq.previous(value) == seq.step(value, -1).map(_._1)
  }

  val sequentialOfOneValue = for {
    value      <- arbitrary[Int]
    sequential <- Gen.const(Sequential.sequential(Vector(value)))
    fromValue  <- arbitrary[Int] retryUntil(_ >= value)
    stepSize   <- arbitrary[Int] retryUntil(_ > 0)
  } yield (value, sequential, fromValue, stepSize)

  property("step on sequential of one element") = forAll(sequentialOfOneValue) {
    case (value, seq, fromValue, stepSize) =>
      seq.step(fromValue, stepSize).contains(value -> stepSize)
  }*/

}
