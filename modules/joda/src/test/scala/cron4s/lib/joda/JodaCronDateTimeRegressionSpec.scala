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

package cron4s.lib.joda

import cron4s._
import org.joda.time.{LocalDateTime, Seconds}
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

/** Created by alonsodomin on 24/02/2017.
  */
class JodaCronDateTimeRegressionSpec extends AnyFlatSpec with Matchers {
  "Cron" should "not advance to the next day" in {
    val from = LocalDateTime.parse("2017-02-18T16:39:42.541")

    val cron = Cron.unsafeParse("* */10 * * * ?")
    val next = cron.next(from).get

    Seconds.secondsBetween(from, next).getSeconds should be <= 600
  }

  it should "reset the milli seconds field" in {
    val from = LocalDateTime.parse("2017-02-18T16:39:42.541")

    val cron = Cron.unsafeParse("* */10 * * * ?")
    val next = cron.next(from).get

    next.getMillisOfSecond shouldBe 0
  }
}
