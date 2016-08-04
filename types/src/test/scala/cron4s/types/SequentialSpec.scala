package cron4s.types

import org.scalacheck._

/**
  * Created by alonsodomin on 04/08/2016.
  */
object SequentialSpec extends Properties("Sequential") {
  import Prop._
  import Arbitrary.arbitrary

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

}
