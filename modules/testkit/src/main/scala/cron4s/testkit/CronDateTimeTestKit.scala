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

package cron4s.testkit

import cats.{Eq, Show}
import cats.implicits._

import cron4s.Cron
import cron4s.datetime.IsDateTime

import org.scalatest.FlatSpec

/**
  * Created by alonsodomin on 29/08/2016.
  */
object CronDateTimeTestKit {

  final val OnlyTuesdaysAt12     = Cron.unsafeParse("0 0 12 ? * 1")
  final val EachMinutesOnSundays = Cron.unsafeParse("0 * * ? * 6")

  // https://github.com/alonsodomin/cron4s/issues/59
  final val BetweenDayOfWeek = Cron.unsafeParse("0 0 0 ? * 1-3")
  final val BetweenMonth     = Cron.unsafeParse("0 0 0 * 4-10 ?")

  // https://github.com/alonsodomin/cron4s/issues/56
  final val Every10Minutes = Cron.unsafeParse("* */10 * * * ?")

  // https://github.com/alonsodomin/cron4s/issues/73
  final val Every31DayOfMonth = Cron.unsafeParse("1 1 1 31 * ?")

  // https://github.com/alonsodomin/cron4s/issues/80
  final val AnyDayOfMonth = Cron.unsafeParse("4 31 4 ? * *")

}

abstract class CronDateTimeTestKit[DateTime: IsDateTime: Eq: Show] extends FlatSpec {
  this: DateTimeTestKitBase[DateTime] =>
  import CronDateTimeTestKit._

  lazy val samples = Seq(
    //("expr",           "from",                              "stepSize", "expected"),
    (
      OnlyTuesdaysAt12,
      createDateTime(0, 0, 0, 1, 8, 2016),
      1,
      createDateTime(0, 0, 12, 2, 8, 2016)
    ),
    (
      EachMinutesOnSundays,
      createDateTime(0, 0, 0, 1, 8, 2016),
      1,
      createDateTime(0, 0, 0, 7, 8, 2016)
    ),
    (
      BetweenDayOfWeek,
      createDateTime(0, 0, 2, 11, 3, 2016),
      1,
      createDateTime(0, 0, 0, 15, 3, 2016)
    ),
    (
      BetweenDayOfWeek,
      createDateTime(0, 0, 2, 6, 3, 2016),
      -1,
      createDateTime(0, 0, 0, 3, 3, 2016)
    ),
    (BetweenMonth, createDateTime(0, 1, 1, 4, 11, 2016), 1, createDateTime(0, 0, 0, 1, 4, 2017)),
    (
      Every10Minutes,
      createDateTime(42, 39, 16, 18, 2, 2017),
      1,
      createDateTime(0, 40, 16, 18, 2, 2017)
    ),
    (
      Every31DayOfMonth,
      createDateTime(0, 0, 0, 4, 9, 2016),
      1,
      createDateTime(1, 1, 1, 31, 10, 2016)
    ),
    (
      AnyDayOfMonth,
      createDateTime(45, 30, 23, 30, 6, 2017),
      1,
      createDateTime(4, 31, 4, 1, 7, 2017)
    )
  )

  "Cron.step" should "match expected result" in {
    val test = Eq[DateTime]

    for ((expr, initial, stepSize, expected) <- samples) {
      val Some(returnedDateTime) = expr.step(initial, stepSize)
      val matchesExpected        = test.eqv(returnedDateTime, expected)

      assert(
        matchesExpected,
        s"[${expr.show}]: (${initial.show}, $stepSize) => ${returnedDateTime.show} != ${expected.show}"
      )
    }
  }

}
