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

package cron4s.lib.javatime

import java.time.LocalDateTime
import java.time.temporal.{ChronoField, ChronoUnit}

import cron4s._

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

/**
  * Created by alonsodomin on 24/02/2017.
  */
class JavaTimeCronDateTimeRegressionSpec extends AnyFlatSpec with Matchers {
  "Cron" should "not advance to the next day" in {
    val from = LocalDateTime.parse("2017-02-18T16:39:42.541")

    val cron = Cron.unsafeParse("* */10 * * * ?")
    val next = cron.next(from).get

    from.until(next, ChronoUnit.SECONDS) <= 600 shouldBe true
  }

  "Cron" should "reset the milli seconds field" in {
    val from = LocalDateTime.parse("2017-02-18T16:39:42.541")

    val cron = Cron.unsafeParse("* */10 * * * ?")
    val next = cron.next(from).get

    next.getLong(ChronoField.MILLI_OF_SECOND) shouldBe 0
  }

  it should "reset previous time * fields" in {
    val from = LocalDateTime.parse("2017-02-18T16:39:42.541")

    val cron = Cron.unsafeParse("* */10 * * * ?")
    val next = cron.next(from).get

    from.until(next, ChronoUnit.SECONDS) shouldBe 17L
  }

  // https://github.com/alonsodomin/cron4s/issues/59
  "Cron with day of week" should "yield a date in the future" in {
    val cron = Cron.unsafeParse("0 0 0 ? * 1-3")

    for (dayOfMonth <- 1 to 30) {
      val from = LocalDateTime.of(2017, 3, dayOfMonth, 2, 0, 0)
      cron.next(from).forall(_.isAfter(from)) shouldBe true
    }
  }
}
