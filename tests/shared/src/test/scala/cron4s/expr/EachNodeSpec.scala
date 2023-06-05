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

package cron4s.expr

import cron4s.CronField
import cron4s.testkit.Cron4sLawSuite
import cron4s.testkit.discipline.FieldExprTests
import cron4s.testkit.gen.ArbitraryEachNode

/** Created by alonsodomin on 31/07/2016.
  */
class EachNodeSpec extends Cron4sLawSuite with ArbitraryEachNode {
  import CronField._

  checkAll("Each[Second]", FieldExprTests[EachNode, Second].expr)
  checkAll("Each[Minute]", FieldExprTests[EachNode, Minute].expr)
  checkAll("Each[Hour]", FieldExprTests[EachNode, Hour].expr)
  checkAll("Each[DayOfMonth]", FieldExprTests[EachNode, DayOfMonth].expr)
  checkAll("Each[Month]", FieldExprTests[EachNode, Month].expr)
  checkAll("Each[DayOfWeek]", FieldExprTests[EachNode, DayOfWeek].expr)
}
