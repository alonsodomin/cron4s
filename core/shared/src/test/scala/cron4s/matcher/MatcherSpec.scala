package cron4s.matcher

import cron4s.types.Predicate
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

  implicit lazy val arbitraryMatcher = Arbitrary[Predicate[Int]] {
    for { x <- arbitrary[Int] } yield equal(x)
  }

  implicit val matcherEquality = Equal.equalBy[Predicate[Int], Boolean](_.apply(0))

  def checkAll(name: String, props: Properties) = {
    for ((name2, prop) <- props.properties) yield {
      property(name + ":" + name2) = prop
    }
  }

  checkAll("Matcher", contravariant.laws[Predicate])

  object disjunction {
    implicit val instance = Predicate.disjunction

    def check() = {
      checkAll("Matcher", plusEmpty.laws[Predicate])
    }
  }
  object conjuction {
    implicit val instance = Predicate.conjunction

    def check() = {
      checkAll("Matcher", plusEmpty.laws[Predicate])
    }
  }

  disjunction.check()
  conjuction.check()

  val matchersAndValues = for {
    matcher <- arbitrary[Predicate[Int]]
    value   <- arbitrary[Int]
  } yield (matcher, value)

  property("not") = forAll(matchersAndValues) {
    case (matcher, value) => (!matcher)(value) == !matcher(value)
  }

  val pairsOfMatchers = for {
    leftMatcher  <- arbitrary[Predicate[Int]]
    rightMatcher <- arbitrary[Predicate[Int]]
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

  val alwaysMatchers = for {
    returnVal <- arbitrary[Boolean]
    matcher   <- Gen.const(always[Int](returnVal))
    value     <- arbitrary[Int]
  } yield (matcher, returnVal, value)

  property("always") = forAll(alwaysMatchers) {
    case (matcher, returnVal, value) => matcher(value) == returnVal
  }

  val negatedMatchers = for {
    matcher <- arbitrary[Predicate[Int]]
    negated <- Gen.const(not(matcher))
    value   <- arbitrary[Int]
  } yield (matcher, negated, value)

  property("negated") = forAll(negatedMatchers) {
    case (matcher, negated, value) =>
      negated(value) == !matcher(value)
  }

  val matcherList = for {
    list  <- Gen.listOf(arbitrary[Predicate[Int]])
    value <- arbitrary[Int]
  } yield (list, value)

  property("none") = forAll(matcherList) {
    case (list, value) => none(list).apply(value) == not(allOf(list))(value)
  }

  property("anyOf") = forAll(matcherList) {
    case (list, value) => anyOf(list).apply(value) == list.exists(_(value))
  }

  property("allOf") = forAll(matcherList) {
    case (list, value) => allOf(list).apply(value) == list.forall(_(value))
  }

}
