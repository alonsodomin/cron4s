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

import cats.Eq

import cron4s._
import cron4s.expr._
import cron4s.datetime.IsDateTime
import cron4s.testkit.discipline.DateTimeNodeTests
import cron4s.testkit.gen._

/** Created by alonsodomin on 04/08/2016.
  */
abstract class DateTimeNodeTestKit[DateTime: IsDateTime: Eq]
    extends Cron4sLawSuite with DateTimeTestKitBase[DateTime] {
  import CronField._

  trait NodeCheck {
    def check(): Unit
  }

  object each extends NodeCheck with ArbitraryEachNode {
    def check() = {
      checkAll(
        "DateTimeNode[EachNode, Second]",
        DateTimeNodeTests[EachNode, Second, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[EachNode, Minute]",
        DateTimeNodeTests[EachNode, Minute, DateTime].dateTime
      )
      checkAll("DateTimeNode[EachNode, Hour]", DateTimeNodeTests[EachNode, Hour, DateTime].dateTime)
      checkAll(
        "DateTimeNode[EachNode, DayOfMonth]",
        DateTimeNodeTests[EachNode, DayOfMonth, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[EachNode, Month]",
        DateTimeNodeTests[EachNode, Month, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[EachNode, DayOfWeek]",
        DateTimeNodeTests[EachNode, DayOfWeek, DateTime].dateTime
      )
    }
  }

  object const extends NodeCheck with ArbitraryConstNode {
    def check() = {
      checkAll(
        "DateTimeNode[ConstNode, Second]",
        DateTimeNodeTests[ConstNode, Second, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[ConstNode, Minute]",
        DateTimeNodeTests[ConstNode, Minute, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[ConstNode, Hour]",
        DateTimeNodeTests[ConstNode, Hour, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[ConstNode, DayOfMonth]",
        DateTimeNodeTests[ConstNode, DayOfMonth, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[ConstNode, Month]",
        DateTimeNodeTests[ConstNode, Month, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[ConstNode, DayOfWeek]",
        DateTimeNodeTests[ConstNode, DayOfWeek, DateTime].dateTime
      )
    }
  }

  object between extends NodeCheck with ArbitraryBetweenNode {
    def check() = {
      checkAll(
        "DateTimeNode[BetweenNode, Second]",
        DateTimeNodeTests[BetweenNode, Second, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[BetweenNode, Minute]",
        DateTimeNodeTests[BetweenNode, Minute, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[BetweenNode, Hour]",
        DateTimeNodeTests[BetweenNode, Hour, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[BetweenNode, DayOfMonth]",
        DateTimeNodeTests[BetweenNode, DayOfMonth, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[BetweenNode, Month]",
        DateTimeNodeTests[BetweenNode, Month, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[BetweenNode, DayOfWeek]",
        DateTimeNodeTests[BetweenNode, DayOfWeek, DateTime].dateTime
      )
    }
  }

  object several extends NodeCheck with ArbitrarySeveralNode {
    def check() = {
      checkAll(
        "DateTimeNode[SeveralNode, Second]",
        DateTimeNodeTests[SeveralNode, Second, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[SeveralNode, Minute]",
        DateTimeNodeTests[SeveralNode, Minute, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[SeveralNode, Hour]",
        DateTimeNodeTests[SeveralNode, Hour, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[SeveralNode, DayOfMonth]",
        DateTimeNodeTests[SeveralNode, DayOfMonth, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[SeveralNode, Month]",
        DateTimeNodeTests[SeveralNode, Month, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[SeveralNode, DayOfWeek]",
        DateTimeNodeTests[SeveralNode, DayOfWeek, DateTime].dateTime
      )
    }
  }

  object every extends NodeCheck with ArbitraryEveryNode {
    def check() = {
      checkAll(
        "DateTimeNode[EveryNode, Second]",
        DateTimeNodeTests[EveryNode, Second, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[EveryNode, Minute]",
        DateTimeNodeTests[EveryNode, Minute, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[EveryNode, Hour]",
        DateTimeNodeTests[EveryNode, Hour, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[EveryNode, DayOfMonth]",
        DateTimeNodeTests[EveryNode, DayOfMonth, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[EveryNode, Month]",
        DateTimeNodeTests[EveryNode, Month, DateTime].dateTime
      )
      checkAll(
        "DateTimeNode[EveryNode, DayOfWeek]",
        DateTimeNodeTests[EveryNode, DayOfWeek, DateTime].dateTime
      )
    }
  }

  for (node <- Seq(each, const, between, several, every)) node.check()
}
