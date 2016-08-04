package cron4s.matcher

import org.scalacheck._

import scalaz._
import Scalaz._
import scalaz.scalacheck.ScalazProperties._

/**
  * Created by alonsodomin on 04/08/2016.
  */
class MatcherSpec extends Properties("Matcher") {
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
    checkAll("Matcher", plusEmpty.laws[Matcher])
  }
  object conjuction {
    implicit val instance = Matcher.conjunction
    checkAll("Matcher", plusEmpty.laws[Matcher])
  }

}
