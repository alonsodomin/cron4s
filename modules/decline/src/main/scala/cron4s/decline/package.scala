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

import cats.data.ValidatedNel
import cats.implicits._

import com.monovore.decline.Argument

package object decline {
  implicit val cronExprArgument: Argument[CronExpr] = new Argument[CronExpr] {
    def defaultMetavar: String = "cron-expr"

    def read(str: String): ValidatedNel[String, CronExpr] =
      Cron(str).leftMap(_.getMessage).toValidatedNel
  }
}
