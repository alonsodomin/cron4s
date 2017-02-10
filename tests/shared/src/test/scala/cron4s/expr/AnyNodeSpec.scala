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
import cron4s.testkit.gen.ArbitratyAnyNode

/**
  * Created by alonsodomin on 10/02/2017.
  */
class AnyNodeSpec extends Cron4sLawSuite with ArbitratyAnyNode {
  import CronField._

  checkAll("Any[Second]", FieldExprTests[AnyNode, Second].expr)
  checkAll("Any[Minute]", FieldExprTests[AnyNode, Minute].expr)
  checkAll("Any[Hour]", FieldExprTests[AnyNode, Hour].expr)
  checkAll("Any[DayOfMonth]", FieldExprTests[AnyNode, DayOfMonth].expr)
  checkAll("Any[Month]", FieldExprTests[AnyNode, Month].expr)
  checkAll("Any[DayOfWeek]", FieldExprTests[AnyNode, DayOfWeek].expr)
}
