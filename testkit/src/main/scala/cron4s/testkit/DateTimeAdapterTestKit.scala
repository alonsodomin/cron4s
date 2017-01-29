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

import cron4s.datetime.DateTimeAdapter
import cron4s.{CronField, CronUnit}
import cron4s.testkit.discipline.DateTimeAdapterTests
import cron4s.testkit.gen.ArbitraryCronFieldValues

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

import scalaz.Equal

/**
  * Created by alonsodomin on 29/08/2016.
  */
abstract class DateTimeAdapterTestKit[DateTime <: AnyRef : DateTimeAdapter : Equal](name: String)
  extends FunSuite with Discipline with ArbitraryCronFieldValues with DateTimeTestKitBase[DateTime] {
  import CronField._
  import CronUnit._

  checkAll(s"DateTimeAdapter[$name, Second]", DateTimeAdapterTests[DateTime].dateTimeAdapter[Second])
  checkAll(s"DateTimeAdapter[$name, Minute]", DateTimeAdapterTests[DateTime].dateTimeAdapter[Minute])
  checkAll(s"DateTimeAdapter[$name, Hour]", DateTimeAdapterTests[DateTime].dateTimeAdapter[Hour])
  checkAll(s"DateTimeAdapter[$name, DayOfMonth]", DateTimeAdapterTests[DateTime].dateTimeAdapter[DayOfMonth])
  checkAll(s"DateTimeAdapter[$name, Month]", DateTimeAdapterTests[DateTime].dateTimeAdapter[Month])
  checkAll(s"DateTimeAdapter[$name, DayOfWeek]", DateTimeAdapterTests[DateTime].dateTimeAdapter[DayOfWeek])

}
