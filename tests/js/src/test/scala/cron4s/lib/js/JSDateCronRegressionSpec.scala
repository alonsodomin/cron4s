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

package cron4s.lib.js

import org.scalatest.{FlatSpec, Matchers}

import cron4s._

import scala.scalajs.js.Date

/**
  * Created by alonsodomin on 25/02/2017.
  */
class JSDateCronRegressionSpec extends FlatSpec with Matchers {

  "Cron" should "not advance to the next day" in {
    val from = new Date("2017-02-18T16:39:42.541")

    val Right(cron) = Cron("* */10 * * * *")
    val Some(next) = cron.next(from)

    (next.getTime() - from.getTime()) <= 600000 shouldBe true
  }

  it should "reset the milli seconds field" in {
    val from = new Date("2017-02-18T16:39:42.541")

    val Right(cron) = Cron("* */10 * * * *")
    val Some(next) = cron.next(from)

    next.getMilliseconds() shouldBe 0
  }

}
