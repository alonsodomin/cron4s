package cron4s.expr

import cron4s.BaseGenerators
import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by alonsodomin on 13/01/2016.
  */
trait ExprGenerators extends BaseGenerators {

  def minuteGen: Gen[String] = Gen.choose(0, 59).map(_.toString)
  implicit def arbitraryMinute = Arbitrary(minuteGen)

}
