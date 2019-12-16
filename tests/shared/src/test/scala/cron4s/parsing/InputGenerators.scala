/*
 * Copyright 2017 Antonio Alonso Dominguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cron4s
package parsing

import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by alonsodomin on 13/01/2016.
  */
trait InputGenerators {
  import Arbitrary._
  import CronUnit._

  // --------------------------------------------------------------
  // Generators of valid parsable values
  // --------------------------------------------------------------

  private[this] def withLeadingZero(base: Int): Gen[String] =
    if (base < 10) {
      arbitrary[Boolean].map { doubleDigit =>
        if (doubleDigit) {
          s"0$base"
        } else base.toString
      }
    } else base.toString

  private[this] def rangedIntGen(min: Int, max: Int): Gen[(String, (Int, Int))] =
    for {
      start    <- Gen.choose(min, max)
      end      <- Gen.choose(start, max)
      startStr <- withLeadingZero(start)
      endStr   <- withLeadingZero(end)
    } yield (s"$startStr-$endStr", (start, end))

  private[this] def sequencedGen[A](
      constGen: Gen[String],
      rangeGen: Gen[(String, (A, A))]
  ): Gen[(String, List[Either[String, (A, A)]])] = {
    val eitherConstOrRange = for {
      const <- constGen.map(v => v -> Left(v))
      range <- rangeGen.map {
        case (input, (start, end)) => input -> Right(start -> end)
      }
      either <- Gen.oneOf(const, range)
    } yield either

    val zero = List.empty[String] -> List.empty[Either[String, (A, A)]]
    Gen
      .nonEmptyListOf(eitherConstOrRange)
      .suchThat(_.size > 1)
      .map(_.foldRight(zero) {
        case ((inputPart, resultPart), (inputList, resultList)) =>
          (inputPart :: inputList) -> (resultPart :: resultList)
      })
      .map {
        case (input, expected) => input.mkString(",") -> expected
      }
  }

  val secondsOrMinutesGen: Gen[String] =
    Gen.choose(0, 59).flatMap(withLeadingZero)
  val secondsOrMinutesRangeGen: Gen[(String, (Int, Int))] =
    rangedIntGen(0, 59)
  val secondsOrMinutesSeqGen: Gen[(String, List[Either[String, (Int, Int)]])] =
    sequencedGen(secondsOrMinutesGen, secondsOrMinutesRangeGen)

  val hoursGen: Gen[String] = Gen.choose(0, 23).flatMap(withLeadingZero)
  val hoursRangeGen: Gen[(String, (Int, Int))] =
    rangedIntGen(0, 23)
  val hoursSeqGen: Gen[(String, List[Either[String, (Int, Int)]])] =
    sequencedGen(hoursGen, hoursRangeGen)

  val daysOfMonthGen: Gen[String] = Gen.choose(1, 31).flatMap(withLeadingZero)
  val daysOfMonthRangeGen: Gen[(String, (Int, Int))] =
    rangedIntGen(1, 31)
  val daysOfMonthSeqGen: Gen[(String, List[Either[String, (Int, Int)]])] =
    sequencedGen(daysOfMonthGen, daysOfMonthRangeGen)

  val numericMonthsGen: Gen[String] = Gen.choose(1, 12).flatMap(withLeadingZero)
  val numericMonthsRangeGen: Gen[(String, (Int, Int))] =
    rangedIntGen(1, 12)
  val numericMonthsSeqGen: Gen[(String, List[Either[String, (Int, Int)]])] =
    sequencedGen(numericMonthsGen, numericMonthsRangeGen)
  val nameMonthsGen: Gen[String] = Gen.oneOf(Months.textValues)
  val namedMonthsRangeGen: Gen[(String, (String, String))] = for {
    start <- Gen.oneOf(Months.textValues)
    end   <- Gen.oneOf(Months.textValues)
  } yield (s"$start-$end", (start, end))
  val namedMonthsSeqGen: Gen[(String, List[Either[String, (String, String)]])] =
    sequencedGen(nameMonthsGen, namedMonthsRangeGen)

  val numericDaysOfWeekGen: Gen[String] = Gen.choose(0, 6).map(_.toString)
  val numericDaysOfWeekRangeGen: Gen[(String, (Int, Int))] = for {
    start <- Gen.choose(0, 6)
    end   <- Gen.choose(start, 6)
  } yield (s"$start-$end", (start, end))
  val numericDaysOfWeekSeqGen: Gen[(String, List[Either[String, (Int, Int)]])] =
    sequencedGen(numericDaysOfWeekGen, numericDaysOfWeekRangeGen)

  val namedDaysOfWeekGen: Gen[String] = Gen.oneOf(DaysOfWeek.textValues)
  val namedDaysOfWeekRangeGen: Gen[(String, (String, String))] = for {
    start <- Gen.oneOf(DaysOfWeek.textValues)
    end   <- Gen.oneOf(DaysOfWeek.textValues)
  } yield (s"$start-$end", (start, end))
  val namedDaysOfWeekSeqGen: Gen[(String, List[Either[String, (String, String)]])] =
    sequencedGen(namedDaysOfWeekGen, namedDaysOfWeekRangeGen)
}
