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

import cats.implicits._

import cron4s.expr.CronExpr

import io.circe.{Encoder, Decoder}

package object circe {

  implicit val cronExprEncoder: Encoder[CronExpr] =
    Encoder[String].contramap(_.show)

  implicit val cronExprDecoder: Decoder[CronExpr] =
    Decoder[String].emap(Cron.onlyParse(_).leftMap(_.getMessage))

}
