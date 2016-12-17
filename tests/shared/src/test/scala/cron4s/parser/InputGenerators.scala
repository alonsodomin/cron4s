package cron4s.parser

import cron4s.CronUnit

import org.scalacheck._

/**
  * Created by alonsodomin on 13/01/2016.
  */
trait InputGenerators {
  import Gen._
  import Arbitrary._
  import CronUnit._

  // --------------------------------------------------------------
  // Generators of valid parsable values
  // --------------------------------------------------------------

  private[this] def withLeadingZero(base: Int): Gen[String] = {
    if (base < 10) {
      arbitrary[Boolean].map { doubleDigit =>
        if (doubleDigit) {
          s"0$base"
        } else base.toString
      }
    } else base.toString
  }

  private[this] def rangedIntInput(min: Int, max: Int): Gen[(String, (Int, Int))] = for {
    start    <- Gen.choose(min, max)
    end      <- Gen.choose(start, max)
    startStr <- withLeadingZero(start)
    endStr   <- withLeadingZero(end)
  } yield (s"$startStr-$endStr", (start, end))

  val secondsOrMinutesGen: Gen[String] = Gen.choose(0, 59).flatMap(withLeadingZero)
  val secondsOrMinutesRangeGen: Gen[(String, (Int, Int))] =
    rangedIntInput(0, 59)

  val hoursGen: Gen[String] = Gen.choose(0, 23).flatMap(withLeadingZero)
  val hoursRangeGen: Gen[(String, (Int, Int))] =
    rangedIntInput(0, 23)

  val daysOfMonthGen: Gen[String] = Gen.choose(1, 31).flatMap(withLeadingZero)
  val daysOfMonthRangeGen: Gen[(String, (Int, Int))] =
    rangedIntInput(1, 31)

  val numericMonthsGen: Gen[String] = Gen.choose(1, 12).flatMap(withLeadingZero)
  val numericMonthsRangeGen: Gen[(String, (Int, Int))] =
    rangedIntInput(1, 12)
  val nameMonthsGen: Gen[String] = Gen.oneOf(Months.textValues)
  val namedMonthsRangeGen: Gen[(String, (String, String))] = for {
    start <- Gen.oneOf(Months.textValues)
    end   <- Gen.oneOf(Months.textValues)
  } yield (s"$start-$end", (start, end))

  val numericDaysOfWeekGen: Gen[String] = Gen.choose(0, 6).map(_.toString)
  val numericDaysOfWeekRangeGen: Gen[(String, (Int, Int))] = for {
    start    <- Gen.choose(0, 6)
    end      <- Gen.choose(start, 6)
  } yield (s"$start-$end", (start, end))

  val namedDaysOfWeekGen: Gen[String] = Gen.oneOf(DaysOfWeek.textValues)
  val namedDaysOfWeekRangeGen: Gen[(String, (String, String))] = for {
    start <- Gen.oneOf(DaysOfWeek.textValues)
    end   <- Gen.oneOf(DaysOfWeek.textValues)
  } yield (s"$start-$end", (start, end))

}
