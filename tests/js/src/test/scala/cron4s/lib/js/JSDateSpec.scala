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

package cron4s.lib.js

import cron4s.testkit.IsDateTimeTestKit
import org.scalacheck.Gen

import scala.scalajs.js.Date

/**
  * Created by alonsodomin on 02/09/2016.
  */
class JSDateSpec extends IsDateTimeTestKit[Date]("JSDate") with JSTestBase {
  // js date implementation has a very specific behavior when setting month : if the day
  // doesn't exist in the target month (say the 31) then setting the month to n is actually
  // setting it to n+1 and it makes property tests to fail
  override protected def genDateTime: Gen[Date] = super.genDateTime.suchThat(_.getDate() < 29)
}
