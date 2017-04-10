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

import cron4s.datetime.IsDateTime

import scala.scalajs.js.Date

/**
  * Created by alonsodomin on 11/12/2016.
  */
package object js {

  implicit val jsDateEq: Eq[Date] = Eq.instance { (lhs, rhs) =>
    lhs.getUTCFullYear() == rhs.getUTCFullYear() &&
      lhs.getUTCMonth() == rhs.getUTCMonth() &&
      lhs.getUTCDate() == rhs.getUTCDate() &&
      lhs.getUTCHours() == rhs.getUTCHours() &&
      lhs.getUTCMinutes() == rhs.getUTCMinutes() &&
      lhs.getUTCSeconds() == rhs.getUTCSeconds() &&
      lhs.getUTCMilliseconds() == rhs.getUTCMilliseconds()
  }

  implicit val jsDateShow: Show[Date] =
    Show.show(_.toISOString())

  implicit val jsDateInstance: IsDateTime[Date] = new JsDateInstance

}
