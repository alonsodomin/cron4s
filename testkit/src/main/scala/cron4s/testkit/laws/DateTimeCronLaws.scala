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

package cron4s.testkit.laws

import cron4s.datetime.{DateTimeAdapter, DateTimeCron}
import cron4s.testkit._
import cron4s.syntax.cron._

/**
  * Created by alonsodomin on 29/01/2017.
  */
trait DateTimeCronLaws[E, DateTime] {
  implicit def adapter: DateTimeAdapter[DateTime]
  implicit def TC: DateTimeCron[E, DateTime]

  def forwards(e: E, from: DateTime): IsEqual[Option[DateTime]] =
    e.next(from) <-> e.step(from, 1)

  def backwards(e: E, from: DateTime): IsEqual[Option[DateTime]] =
    e.prev(from) <-> e.step(from, -1)

}

object DateTimeCronLaws {
  def apply[E, DateTime](implicit
    adapter0: DateTimeAdapter[DateTime],
    TC0: DateTimeCron[E, DateTime]
  ): DateTimeCronLaws[E, DateTime] =
    new DateTimeCronLaws[E, DateTime] {
      implicit val adapter = adapter0
      implicit val TC = TC0
    }
}
