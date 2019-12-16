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

package cron4s.lib

import cats.{Eq, Show}
import cats.instances.double._

import cron4s.datetime.IsDateTime

import moment._

/**
  * Created by alonsodomin on 11/04/2017.
  */
package object momentjs {
  implicit val momentjsEq: Eq[Date] = Eq.by(_.unix())

  implicit val momentjsShow: Show[Date] = Show.show(_.toISOString())

  implicit val momentjsInstance: IsDateTime[Date] = new MomentJSInstance
}
