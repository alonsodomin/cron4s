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

import cron4s.datetime.IsDateTime
import cron4s.expr.{CronExpr, DateCronExpr, TimeCronExpr}
import cron4s.testkit.discipline.DateTimeCronTests
import cron4s.testkit.gen.CronGenerators

/**
  * Created by alonsodomin on 29/01/2017.
  */
abstract class DateTimeCronTestKit[DateTime: IsDateTime: Eq]
    extends SlowCron4sLawSuite with DateTimeTestKitBase[DateTime] with CronGenerators {
  checkAll("CronExpr", DateTimeCronTests[CronExpr, DateTime].dateTimeCron)
  checkAll("TimeCronExpr", DateTimeCronTests[DateCronExpr, DateTime].dateTimeCron)
  checkAll("DateCronExpr", DateTimeCronTests[TimeCronExpr, DateTime].dateTimeCron)
}
