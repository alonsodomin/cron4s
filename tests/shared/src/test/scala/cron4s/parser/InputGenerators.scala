package cron4s.parser

import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by alonsodomin on 13/01/2016.
  */
trait InputGenerators {



  def minuteGen: Gen[String] = Gen.choose(0, 59).map(_.toString)
  implicit def arbitraryMinute = Arbitrary(minuteGen)

}
