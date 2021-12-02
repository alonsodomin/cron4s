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

package cron4s.bugs

import cron4s.lib.javatime.javaTemporalInstance
import cron4s.{Cron, CronExpr}
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

import java.time.{Instant, ZoneOffset, ZonedDateTime}

class BugSpec extends AnyFlatSpec with Matchers {

  "Cron" should "get nonempty prev on 1st of month where previous month has fewer than 31 days" in {
    val expr: CronExpr = Cron.unsafeParse("0 3 15 ? * 0-3")
    expr.prev(ZonedDateTime.ofInstant(Instant.parse("2021-03-01T00:03:00.00Z"), ZoneOffset.UTC)).nonEmpty shouldBe true
    expr.prev(ZonedDateTime.ofInstant(Instant.parse("2021-05-01T00:03:00.00Z"), ZoneOffset.UTC)).nonEmpty shouldBe true
    expr.prev(ZonedDateTime.ofInstant(Instant.parse("2021-07-01T00:03:00.00Z"), ZoneOffset.UTC)).nonEmpty shouldBe true
    expr.prev(ZonedDateTime.ofInstant(Instant.parse("2021-10-01T00:03:00.00Z"), ZoneOffset.UTC)).nonEmpty shouldBe true
    expr.prev(ZonedDateTime.ofInstant(Instant.parse("2021-12-01T00:03:00.00Z"), ZoneOffset.UTC)).nonEmpty shouldBe true
  }
}