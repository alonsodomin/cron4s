package cron4s.matcher

import org.scalacheck._

import scalaz._
import Scalaz._
import scalaz.scalacheck.ScalazProperties._

/**
  * Created by alonsodomin on 04/08/2016.
  */
object MatcherSpec extends Properties("Matcher") {
  import Prop._
  import Arbitrary.arbitrary

  implicit lazy val arbitraryMatcher = Arbitrary[Matcher[Int]] {
    for { x <- arbitrary[Int] } yield equal(x)
  }

  implicit val matcherEquality = Equal.equalBy[Matcher[Int], Boolean](_.apply(0))

  def checkAll(name: String, props: Properties) = {
    for ((name2, prop) <- props.properties) yield {
      property(name + ":" + name2) = prop
    }
  }

  checkAll("Matcher", contravariant.laws[Matcher])

  object disjunction {
    implicit val instance = Matcher.disjunction

    def check() = {
      checkAll("Matcher", plusEmpty.laws[Matcher])
    }
  }
  object conjuction {
    implicit val instance = Matcher.conjunction

    def check() = {
      checkAll("Matcher", plusEmpty.laws[Matcher])
    }
  }

  disjunction.check()
  conjuction.check()

  val matchersAndValues = for {
    matcher <- arbitrary[Matcher[Int]]
    value   <- arbitrary[Int]
  } yield (matcher, value)

  property("not") = forAll(matchersAndValues) {
    case (matcher, value) => !matcher(value) == {
      val res = matcher(value)
      !res
    }
  }

  val pairsOfMatchers = for {
    leftMatcher  <- arbitrary[Matcher[Int]]
    rightMatcher <- arbitrary[Matcher[Int]]
    value        <- arbitrary[Int]
  } yield (leftMatcher, rightMatcher, value)

  property("and") = forAll(pairsOfMatchers) {
    case (left, right, value) =>
      (left && right)(value) == (left(value) && right(value))
  }

  property("or") = forAll(pairsOfMatchers) {
    case (left, right, value) =>
      (left || right)(value) == (left(value) || right(value))
  }

}
