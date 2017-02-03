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
import cron4s.testkit.SlowCron4sLawSuite
import cron4s.testkit.discipline.ExprTests
import cron4s.testkit.gen.ArbitrarySeveralNode


/**
  * Created by alonsodomin on 01/08/2016.
  */
class SeveralNodeSpec extends SlowCron4sLawSuite with ArbitrarySeveralNode {

  checkAll("Several[Second]", ExprTests[SeveralNode, Second].expr)
  checkAll("Several[Minute]", ExprTests[SeveralNode, Minute].expr)
  checkAll("Several[Hour]", ExprTests[SeveralNode, Hour].expr)
  checkAll("Several[DayOfMonth]", ExprTests[SeveralNode, DayOfMonth].expr)
  checkAll("Several[Month]", ExprTests[SeveralNode, Month].expr)
  checkAll("Several[DayOfWeek]", ExprTests[SeveralNode, DayOfWeek].expr)

}
