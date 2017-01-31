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
import cron4s.testkit.discipline.ExprTests
import cron4s.testkit.gen.ArbitraryConstNode

import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

/**
  * Created by alonsodomin on 31/07/2016.
  */
class ConstNodeSpec extends FunSuite with Discipline with ArbitraryConstNode {
  import CronField._

  checkAll("Const[Second]", ExprTests[ConstNode, Second].expr)
  checkAll("Const[Minute]", ExprTests[ConstNode, Minute].expr)
  checkAll("Const[Hour]", ExprTests[ConstNode, Hour].expr)
  checkAll("Const[DayOfMonth]", ExprTests[ConstNode, DayOfMonth].expr)
  checkAll("Const[Month]", ExprTests[ConstNode, Month].expr)
  checkAll("Const[DayOfWeek]", ExprTests[ConstNode, DayOfWeek].expr)

}
