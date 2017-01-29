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

package cron4s.testkit.gen

import cron4s.CronField
import cron4s.expr.{CronExpr, DateCronExpr, TimeCronExpr}

import org.scalacheck._

/**
  * Created by alonsodomin on 29/01/2017.
  */
trait CronGenerators extends NodeGenerators {

  private[this] val fullCronGen = for {
    seconds     <- nodeGen[CronField.Second]
    minutes     <- nodeGen[CronField.Minute]
    hours       <- nodeGen[CronField.Hour]
    daysOfMonth <- nodeGen[CronField.DayOfMonth]
    months      <- nodeGen[CronField.Month]
    daysOfWeek  <- nodeGen[CronField.DayOfWeek]
  } yield CronExpr(seconds, minutes, hours, daysOfMonth, months, daysOfWeek)

  private[this] val timeCronGen = for {
    seconds     <- nodeGen[CronField.Second]
    minutes     <- nodeGen[CronField.Minute]
    hours       <- nodeGen[CronField.Hour]
  } yield TimeCronExpr(seconds, minutes, hours)

  private[this] val dateCronGen = for {
    daysOfMonth <- nodeGen[CronField.DayOfMonth]
    months      <- nodeGen[CronField.Month]
    daysOfWeek  <- nodeGen[CronField.DayOfWeek]
  } yield DateCronExpr(daysOfMonth, months, daysOfWeek)

  implicit lazy val arbitraryFullCron: Arbitrary[CronExpr]     = Arbitrary(fullCronGen)
  implicit lazy val arbitraryTimeCron: Arbitrary[TimeCronExpr] = Arbitrary(timeCronGen)
  implicit lazy val arbitraryDateCron: Arbitrary[DateCronExpr] = Arbitrary(dateCronGen)

}
