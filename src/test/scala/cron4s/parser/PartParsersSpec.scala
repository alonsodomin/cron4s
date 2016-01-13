package cron4s.parser

import org.scalacheck._

/**
  * Created by alonsodomin on 13/01/2016.
  */
object PartParsersSpec extends Properties("PartParsers") with PartParsers with PartGenerators {
  import Prop._

  property("Should be able to parse minutes") = forAll(Gen.choose(0, 59)) {
    x => parseAll(minute, x.toString) match {
      case Success(v, _) => v.matcher.matches(x)
      case NoSuccess(_, _) => false
    }
  }

}
