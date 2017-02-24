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

import cron4s.CronField._
import cron4s.testkit.Cron4sLawSuite
import cron4s.testkit.discipline.FieldExprTests
import cron4s.testkit.gen.ArbitraryBetweenNode

/**
  * Created by alonsodomin on 31/07/2016.
  */
class BetweenNodeSpec extends Cron4sLawSuite with ArbitraryBetweenNode {

  checkAll("Between[Second]", FieldExprTests[BetweenNode, Second].expr)
  checkAll("Between[Minute]", FieldExprTests[BetweenNode, Minute].expr)
  checkAll("Between[Hour]", FieldExprTests[BetweenNode, Hour].expr)
  checkAll("Between[DayOfMonth]", FieldExprTests[BetweenNode, DayOfMonth].expr)
  checkAll("Between[Month]", FieldExprTests[BetweenNode, Month].expr)
  checkAll("Between[DayOfWeek]", FieldExprTests[BetweenNode, DayOfWeek].expr)

}
