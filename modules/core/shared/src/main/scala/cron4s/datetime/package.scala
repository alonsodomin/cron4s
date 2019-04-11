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

import cron4s.expr.{DateCronExpr, TimeCronExpr}

import shapeless._

import scala.language.implicitConversions

/**
  * Created by alonsodomin on 24/01/2017.
  */
package object datetime {
  import CronField._

  private[datetime] type AnyCron =
    CronExpr :+: TimeCronExpr :+: DateCronExpr :+: CNil

  private[datetime] implicit def cronExpr2AnyCron(cron: CronExpr): AnyCron =
    Coproduct[AnyCron](cron)

  private[datetime] implicit def timeExpr2AnyCron(cron: TimeCronExpr): AnyCron =
    Coproduct[AnyCron](cron)

  private[datetime] implicit def dateExpr2AnyCron(cron: DateCronExpr): AnyCron =
    Coproduct[AnyCron](cron)

  private[datetime] type FieldSeq =
    Second :: Minute :: Hour :: DayOfMonth :: Month :: DayOfWeek :: HNil
  val FieldSeq: FieldSeq = Second :: Minute :: Hour :: DayOfMonth :: Month :: DayOfWeek :: HNil

}
