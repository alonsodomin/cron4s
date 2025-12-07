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

package cron4s

import cats.{Eq, Show}

/**
  * Each of the different fields supported in CRON expressions
  *
  * @author Antonio Alonso Dominguez
  */
sealed trait CronField extends Serializable
object CronField       extends CronFieldInstances {
  sealed trait Second extends CronField
  case object Second  extends Second

  sealed trait Minute extends CronField
  case object Minute  extends Minute

  sealed trait Hour extends CronField
  case object Hour  extends Hour

  sealed trait DayOfMonth extends CronField
  case object DayOfMonth  extends DayOfMonth

  sealed trait Month extends CronField
  case object Month  extends Month

  sealed trait DayOfWeek extends CronField
  case object DayOfWeek  extends DayOfWeek
  sealed trait Year extends CronField
  case object Year  extends Year

  final val All: List[CronField] =
    List(Second, Minute, Hour, DayOfMonth, Month, DayOfWeek, Year)
}

private[cron4s] trait CronFieldInstances {
  implicit val cronFieldEq: Eq[CronField] = Eq.fromUniversalEquals[CronField]

  implicit val cronFieldShow: Show[CronField] = Show.fromToString
}
